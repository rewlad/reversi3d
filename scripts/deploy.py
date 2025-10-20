#!/usr/bin/env python3
"""Deploy Reversi 3D to Kubernetes without editing manifests."""

import argparse
import json
import subprocess
import sys
from typing import List, Tuple


def kubectl_base(context: str) -> Tuple[str, ...]:
    return ("kubectl", "--context", context)


def kubectl_apply(manifests: List[dict], context: str) -> None:
    command = (*kubectl_base(context), "apply", "-f", "-")
    payload = "\n".join(json.dumps(m) for m in manifests).encode("utf-8")
    subprocess.run(command, check=True, input=payload)


def parse_args(argv: List[str]) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="Apply the Reversi 3D deployment using kubectl"
    )
    parser.add_argument("--context", required=True, help="kubectl context name")
    parser.add_argument("--image", help="container image to deploy")
    parser.add_argument(
        "--image-head",
        action="store_true",
        help="use ghcr.io/<owner>/<repo>:<git-head> (requires git)"
    )
    parser.add_argument("--host", required=True, help="ingress hostname")
    parser.add_argument(
        "--tls-secret",
        default="reversi-tls",
        help="TLS secret for the ingress (default: reversi-tls)",
    )
    parser.add_argument(
        "--skip-ingress",
        action="store_true",
        help="do not create or update the ingress",
    )
    return parser.parse_args(argv)


def current_image(repo: str) -> str:
    sha = (
        subprocess.check_output(["git", "rev-parse", "HEAD"], text=True)
        .strip()
    )
    return f"ghcr.io/{repo}:{sha}"


def build_ingress(host: str, tls_secret: str) -> dict:
    return {
        "apiVersion": "networking.k8s.io/v1",
        "kind": "Ingress",
        "metadata": {
            "name": "reversi3d",
            "annotations": {
                "kubernetes.io/ingress.class": "nginx",
                "nginx.ingress.kubernetes.io/backend-protocol": "HTTP",
            },
        },
        "spec": {
            "tls": [
                {
                    "hosts": [host],
                    "secretName": tls_secret,
                }
            ],
            "rules": [
                {
                    "host": host,
                    "http": {
                        "paths": [
                            {
                                "path": "/",
                                "pathType": "Prefix",
                                "backend": {
                                    "service": {
                                        "name": "reversi3d",
                                        "port": {"number": 80},
                                    }
                                },
                            }
                        ]
                    },
                }
            ],
        },
    }


def build_manifests(image: str, host: str, tls_secret: str, include_ingress: bool) -> List[dict]:
    deployment = {
        "apiVersion": "apps/v1",
        "kind": "Deployment",
        "metadata": {"name": "reversi3d", "labels": {"app": "reversi3d"}},
        "spec": {
            "replicas": 1,
            "selector": {"matchLabels": {"app": "reversi3d"}},
            "template": {
                "metadata": {"labels": {"app": "reversi3d"}},
                "spec": {
                    "containers": [
                        {
                            "name": "reversi3d",
                            "image": image,
                            "imagePullPolicy": "IfNotPresent",
                            "ports": [{"containerPort": 8080}],
                            "readinessProbe": {
                                "httpGet": {"path": "/reversi.html", "port": 8080},
                                "initialDelaySeconds": 10,
                                "periodSeconds": 10,
                            },
                            "livenessProbe": {
                                "httpGet": {"path": "/reversi.html", "port": 8080},
                                "initialDelaySeconds": 30,
                                "periodSeconds": 30,
                            },
                            "resources": {
                                "requests": {"cpu": "50m", "memory": "128Mi"},
                            },
                        }
                    ]
                },
            },
        },
    }

    service = {
        "apiVersion": "v1",
        "kind": "Service",
        "metadata": {"name": "reversi3d", "labels": {"app": "reversi3d"}},
        "spec": {
            "selector": {"app": "reversi3d"},
            "ports": [
                {"name": "http", "port": 80, "targetPort": 8080}
            ],
        },
    }

    return [
        deployment,
        service,
        *(() if not include_ingress else (build_ingress(host, tls_secret),)),
    ]


def main(argv: List[str]) -> None:
    args = parse_args(argv)

    if args.image_head:
        repo = (
            subprocess.check_output(["git", "config", "--get", "remote.origin.url"], text=True)
            .strip()
        )
        repo_path = repo.split(":")[-1].split("github.com/")[-1].replace(".git", "")
        image = current_image(repo_path)
    elif args.image:
        image = args.image
    else:
        raise SystemExit("--image or --image-head required")

    manifests = build_manifests(image, args.host, args.tls_secret, not args.skip_ingress)
    kubectl_apply(manifests, args.context)


if __name__ == "__main__":
    try:
        main(sys.argv[1:])
    except subprocess.CalledProcessError as exc:
        raise SystemExit(exc.returncode) from exc
