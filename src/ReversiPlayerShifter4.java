import java.util.*;

public class ReversiPlayerShifter4 implements ReversiPlayer {
    final static int setbestmove = 1;
    final static int was_skipped = 2;
    final static int simple_depth = 2;
    int my_time_limit = 900;
    int my_depth_limit = 30;
    long[][][] movestack = new long[my_depth_limit+1][64][3];
    long time2finish = 0;
    int finish_counter = 0;
    boolean depth_inc_has_sense;
    long my_bestmove=0;
    
    public long findBestMove( long mover_board, long opponent_board , long move_board ) {
        long bestmove = my_bestmove = 0;
        try{            
            time2finish = Calendar.getInstance().getTimeInMillis()+my_time_limit;
            depth_inc_has_sense = true;
            for( int depth=simple_depth+1; depth_inc_has_sense && depth<=my_depth_limit; depth++){
                depth_inc_has_sense = false;
                estimate( 
                    mover_board, 
                    opponent_board, 
                    depth, setbestmove, -Integer.MAX_VALUE, Integer.MAX_VALUE 
                );
                System.out.print(depth);
                bestmove = my_bestmove;
            }
        } catch(InterruptedException e){}
        return bestmove;
    }
    
    final private int estimate_heur_side(long free, long mb, long ob){ return (int) (
         Long.bitCount(mb    & 0x8100000000000081L)*16 //corner
        +Long.bitCount(mb    & 0xFF818181818181FFL)*4 //side
        -(((free & ( (mb  <<9)|(mb  <<8)|(mb   <<1) ))>>>(8*7+7)) & 1)*10 //near corner nw
        -(((free & ( (mb  <<7)|(mb  <<8)|(mb  >>>1) ))>>>(8*7+0)) & 1)*10 //near corner ne
        -(((free & ( (mb >>>7)|(mb >>>8)|(mb   <<1) ))>>>(8*0+7)) & 1)*10 //near corner sw
        -(((free & ( (mb >>>9)|(mb >>>8)|(mb  >>>1) ))>>>(8*0+0)) & 1)*10 //near corner se
        +Long.bitCount(ReversiTransform.findMoves(mb,ob))*3        
    );}
    
    
    final private int estimate( 
        long mover_board, 
        long opponent_board,
        int depth,
        int mode,
        int mover_best,
        int opponent_best
    ) throws InterruptedException {    
        if(mover_board==0) return -10000000; //lost
        if(opponent_board==0) return 10000000; //win
        
        //heuristic
        if(depth==0){
            long free_board = ~ ( mover_board | opponent_board );
            return Long.bitCount(free_board)<11 ?
                Long.bitCount(mover_board) - Long.bitCount(opponent_board) :
                estimate_heur_side(free_board,mover_board,opponent_board) - 
                estimate_heur_side(free_board,opponent_board,mover_board);
        }
        
        // move count
        long move_board = ReversiTransform.findMoves(mover_board,opponent_board);
        if(move_board==0){
        
            //no move
            if((mode & was_skipped)==0) return - estimate( 
                opponent_board,
                mover_board,
                depth, 
                was_skipped,
                -opponent_best,
                -mover_best
            );
                
            //both can't move
            int mover_count = Long.bitCount(mover_board);
            int opponent_count = Long.bitCount(opponent_board);
            return 
                mover_count>opponent_count ?  10000000 : 
                mover_count<opponent_count ? -10000000 : -1000000;
        }
        
        // moves
        if( depth > simple_depth ){
            int move_count = 0;
            
            for( long cur_cell=1 ; cur_cell!=0 ; cur_cell<<=1 ){
                if( (cur_cell & move_board)==0 ) continue;
                long all_flip = ReversiTransform.doMove(mover_board,opponent_board,cur_cell);
                long[] cur_move = movestack[depth][move_count];
                cur_move[0] = cur_cell;
                cur_move[1] = all_flip;
                cur_move[2] = - estimate( 
                    opponent_board & ~all_flip, 
                    mover_board | all_flip, 
                    0,0,0,0
                );
                
                int ins_pos = move_count;
                while( ins_pos>0 && movestack[depth][ins_pos-1][2] < cur_move[2] ){                    
                    movestack[depth][ins_pos] = movestack[depth][ins_pos-1];
                    ins_pos--;
                }
                movestack[depth][ins_pos] = cur_move;
                
                move_count++;                
            }
            
            if( finish_counter-- <= 0 ){
                finish_counter = 100;
                if( time2finish < Calendar.getInstance().getTimeInMillis() ) 
                    throw new InterruptedException();
            }
            
            for(int i=0;i<move_count;i++){
                long cur_cell = movestack[depth][i][0];
                long all_flip = movestack[depth][i][1];
            
                int score = - estimate( 
                    opponent_board & ~all_flip, 
                    mover_board | all_flip, 
                    depth-1, 
                    0,
                    -opponent_best,
                    -mover_best
                );
                if( mover_best < score ){
                    mover_best = score;
                    if( mover_best>=opponent_best ) break;
                    if((mode & setbestmove)!=0) my_bestmove = cur_cell;
                }
                
            }
            
        } else {
            if(depth==1 && !depth_inc_has_sense) depth_inc_has_sense = true;
            for( long cur_cell=1 ; cur_cell!=0 ; cur_cell<<=1 ){
                if( (cur_cell & move_board)==0 ) continue;
                long all_flip = ReversiTransform.doMove(mover_board,opponent_board,cur_cell);
                int score = - estimate( 
                    opponent_board & ~all_flip, 
                    mover_board | all_flip, 
                    depth-1, 
                    0,
                    -opponent_best,
                    -mover_best
                );
                if( mover_best < score ){
                    mover_best = score;
                    if( mover_best>=opponent_best ) break;
                }               
            }            
        }
        
        return mover_best;
    }
    
    void printboard( long mover_board, long opponent_board ) {  
        for (int row = 0; row < 8; row ++) {
            for (int col = 0; col < 8; col ++) {
                System.out.print(mover_board<0?"m":opponent_board<0?"o":".");              
                mover_board<<=1;   
                opponent_board<<=1;   
            }
            System.out.println();
        }
        System.out.println();        
    }    

	
}