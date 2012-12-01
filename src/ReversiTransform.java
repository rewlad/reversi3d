
public class ReversiTransform {
    final static long findMoves(
        long mover_board, 
        long opponent_board
    ){
        long free_board = ~ (mover_board | opponent_board);
        long move_board = 0;
        long capturable, dir_flip;        
        
        capturable = opponent_board & 0x007F7F7F7F7F7F7FL;
        dir_flip = ((mover_board & 0x007F7F7F7F7F7F7FL) <<9) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<9)) & capturable;
                
        move_board |= (dir_flip <<9) & free_board;
        
        capturable = opponent_board & 0x00FFFFFFFFFFFFFFL;
        dir_flip = ((mover_board & 0x00FFFFFFFFFFFFFFL) <<8) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<8)) & capturable;
                
        move_board |= (dir_flip <<8) & free_board;
        
        capturable = opponent_board & 0x00FEFEFEFEFEFEFEL;
        dir_flip = ((mover_board & 0x00FEFEFEFEFEFEFEL) <<7) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<7)) & capturable;
                
        move_board |= (dir_flip <<7) & free_board;
        
        capturable = opponent_board & 0x7F7F7F7F7F7F7F7FL;
        dir_flip = ((mover_board & 0x7F7F7F7F7F7F7F7FL) <<1) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip <<1)) & capturable;
                
        move_board |= (dir_flip <<1) & free_board;
        
        capturable = opponent_board & 0xFEFEFEFEFEFEFEFEL;
        dir_flip = ((mover_board & 0xFEFEFEFEFEFEFEFEL) >>>1) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>1)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>1)) & capturable;
                
        move_board |= (dir_flip >>>1) & free_board;
        
        capturable = opponent_board & 0x7F7F7F7F7F7F7F00L;
        dir_flip = ((mover_board & 0x7F7F7F7F7F7F7F00L) >>>7) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>7)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>7)) & capturable;
                
        move_board |= (dir_flip >>>7) & free_board;
        
        capturable = opponent_board & 0xFFFFFFFFFFFFFF00L;
        dir_flip = ((mover_board & 0xFFFFFFFFFFFFFF00L) >>>8) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>8)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>8)) & capturable;
                
        move_board |= (dir_flip >>>8) & free_board;
        
        capturable = opponent_board & 0xFEFEFEFEFEFEFE00L;
        dir_flip = ((mover_board & 0xFEFEFEFEFEFEFE00L) >>>9) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>9)) & capturable;
        
        dir_flip = (dir_flip | (dir_flip >>>9)) & capturable;
                
        move_board |= (dir_flip >>>9) & free_board;
        
        return move_board;
    }
    
    final static long doMove(
        long mover_board, 
        long opponent_board,
        long cur_cell
    ){
        long all_flip = cur_cell;
        long dir_flip;
        
        if( 
            ( cur_cell & 0x007F7F7F7F7F7F7FL )!=0 && 
            ( dir_flip = ( cur_cell <<9 ) & opponent_board & 0x007F7F7F7F7F7F7FL )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip <<9); }
            while( ( dir_flip & opponent_board & 0x007F7F7F7F7F7F7FL ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0x00FFFFFFFFFFFFFFL )!=0 && 
            ( dir_flip = ( cur_cell <<8 ) & opponent_board & 0x00FFFFFFFFFFFFFFL )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip <<8); }
            while( ( dir_flip & opponent_board & 0x00FFFFFFFFFFFFFFL ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0x00FEFEFEFEFEFEFEL )!=0 && 
            ( dir_flip = ( cur_cell <<7 ) & opponent_board & 0x00FEFEFEFEFEFEFEL )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip <<7); }
            while( ( dir_flip & opponent_board & 0x00FEFEFEFEFEFEFEL ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0x7F7F7F7F7F7F7F7FL )!=0 && 
            ( dir_flip = ( cur_cell <<1 ) & opponent_board & 0x7F7F7F7F7F7F7F7FL )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip <<1); }
            while( ( dir_flip & opponent_board & 0x7F7F7F7F7F7F7F7FL ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0xFEFEFEFEFEFEFEFEL )!=0 && 
            ( dir_flip = ( cur_cell >>>1 ) & opponent_board & 0xFEFEFEFEFEFEFEFEL )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip >>>1); }
            while( ( dir_flip & opponent_board & 0xFEFEFEFEFEFEFEFEL ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0x7F7F7F7F7F7F7F00L )!=0 && 
            ( dir_flip = ( cur_cell >>>7 ) & opponent_board & 0x7F7F7F7F7F7F7F00L )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip >>>7); }
            while( ( dir_flip & opponent_board & 0x7F7F7F7F7F7F7F00L ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0xFFFFFFFFFFFFFF00L )!=0 && 
            ( dir_flip = ( cur_cell >>>8 ) & opponent_board & 0xFFFFFFFFFFFFFF00L )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip >>>8); }
            while( ( dir_flip & opponent_board & 0xFFFFFFFFFFFFFF00L ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        if( 
            ( cur_cell & 0xFEFEFEFEFEFEFE00L )!=0 && 
            ( dir_flip = ( cur_cell >>>9 ) & opponent_board & 0xFEFEFEFEFEFEFE00L )!=0 
        ){
            do{ dir_flip = dir_flip | (dir_flip >>>9); }
            while( ( dir_flip & opponent_board & 0xFEFEFEFEFEFEFE00L ) == dir_flip );
            if( (dir_flip & mover_board)!=0 ) all_flip |= dir_flip;				
        }
        
        return all_flip;
    }
}
