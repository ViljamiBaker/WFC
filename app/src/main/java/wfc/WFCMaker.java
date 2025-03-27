package wfc;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.errorprone.annotations.OverridingMethodsMustInvokeSuper;

public class WFCMaker {
    // the grid of ints to output
    int[][] grid;
    // directions that are concidered neighbors
    static int[][] dirs = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

    WFCMaker.Tile[] tiles;

    public WFCMaker(int[][] startingGrid, int xsize, int ysize){
        System.out.println(arrToString(startingGrid));
        grid = new int[xsize][ysize];
        ArrayList<WFCMaker.Tile> newTiles = new ArrayList<>();
        // generate the tiles
        for (int y = 0; y < startingGrid.length; y++) {
            for (int x = 0; x < startingGrid[0].length; x++) {
                // if we have not seen this type before
                if(!newTiles.contains(new Tile(startingGrid[y][x]))){
                    // add it
                    newTiles.add(new Tile(startingGrid[y][x]));
                }
                Tile t = newTiles.get(newTiles.indexOf(new Tile(startingGrid[y][x])));
                int[] arrangement = new int[dirs.length];
                // check if this is a new arrangement
                for (int i = 0; i < arrangement.length; i++) {
                    arrangement[i] = getTileAt(startingGrid, x+ dirs[i][0], y+ dirs[i][1]);
                }
                boolean newArrangement = t.validNeighbors.isEmpty();
                for (int[] arr : t.validNeighbors) {
                    if (!Arrays.equals(arrangement, arr)) {
                        newArrangement = true;
                        break;
                    }
                }
                // add it if it is
                if(newArrangement){
                    t.addValidNeighbor(arrangement);
                }
            }
        }
        // done generating the tiles
        tiles = newTiles.toArray(new Tile[0]);
        for (Tile tile : newTiles) {
            System.out.println(tile);
        }
    }

    private class Tile{
        // what type is this tile
        int type;
        // what neighbor arrangements are vaid
        ArrayList<int[]> validNeighbors = new ArrayList<>();

        public Tile(int type){
            this.type = type;
        }

        public void addValidNeighbor(int[] arrangement){
            validNeighbors.add(arrangement);
        }

        public boolean checkPos(int x, int y){
            //for each of the arrangements
            for (int[] arrangement : validNeighbors) {
                // check the dirs
                for (int d = 0; d < dirs.length; d++) {
                    // ignore -1
                    if(getTileAt(x+dirs[d][0], y+ dirs[d][1]) == -1 || arrangement[d] == -1 ) continue;
                    // if the tile at the dir is not the same as the arrangement say no
                    if(getTileAt(x+dirs[d][0], y+ dirs[d][1]) != arrangement[d]) return false;
                }
            }
            return true;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Tile)) return false;
            return ((Tile)o).type == this.type;
        }

        @Override
        public String toString(){
            String s = "Tile of type: " + type + " \nvalid arr: \n";
            s += arrToString(validNeighbors.toArray(new int[0][0]));
            return s;
        }
    }

    protected int getTileAt(int x, int y){
        if(x<0||x>=grid[0].length||y<0||y>=grid.length){
            return -1;
        }
        return grid[y][x];
    }

    protected int getTileAt(int[][] grid, int x, int y){
        if(x<0||x>=grid[0].length||y<0||y>=grid.length){
            return -1;
        }
        return grid[y][x];
    }

    private String arrToString(int[][] arr){
        String s = "[\n";
        for (int y = 0; y < arr.length; y++) {
            s += "  [";
            for (int x = 0; x < arr[0].length; x++) {
                s+= arr[y][x];
                if(x!=arr[0].length-1) s+=", ";
            }
            s += "]\n";
        }
        s+= "]";
        return s;
    }

    public static void main(String[] args) {
        try {Thread.sleep(100);} catch (Exception e) {}
        WFCMaker wfc = new WFCMaker(new int[][] 
        {
            {1,1,1,1,1},
            {1,0,1,0,1},
            {1,1,0,1,1},
            {1,0,1,0,1},
            {1,1,1,1,1},
        }
        
        ,10, 10);
    }
}