package wfc;

import java.net.InterfaceAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.checkerframework.checker.index.qual.GTENegativeOne;

public class WFCMaker {
    // the grid of ints to output
    int[][] grid;
    // directions that are concidered neighbors
    static int[][] dirs = 
    //{{1,0},{0,1},{-1,0},{0,-1}};
    {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

    WFCMaker.Tile[] tiles;

    Random r;

    double reqPercent = 8.0/8.0;

    int xsize;

    int ysize;

    public WFCMaker(int[][] startingGrid, int xsize, int ysize, long seed){
        if (seed != -1){
            r = new Random(seed);
        }else{
            r = new Random();
        }
        this.xsize = xsize;
        this.ysize = ysize;
        grid = new int[ysize][xsize];
        ArrayList<WFCMaker.Tile> newTiles = new ArrayList<>();
        // generate the tiles
        for (int y = 0; y < startingGrid.length; y++) {
            for (int x = 0; x < startingGrid[0].length; x++) {
                // if we have not seen this type before
                if(!newTiles.contains(new Tile(startingGrid[x][y]))){
                    // add it
                    newTiles.add(new Tile(startingGrid[x][y]));
                }
                Tile t = newTiles.get(newTiles.indexOf(new Tile(startingGrid[x][y])));
                int[] arrangement = new int[dirs.length];
                // check if this is a new arrangement
                for (int i = 0; i < arrangement.length; i++) {
                    arrangement[i] = getTileAt(startingGrid, x+ dirs[i][0], y+ dirs[i][1]);
                }
                boolean newArrangement = t.validNeighbors.isEmpty();
                for (int[] arr : t.validNeighbors) {
                    boolean na = false;
                    for (int i = 0; i < dirs.length; i++) {
                        if(arr[i]!=arrangement[i]){
                            na = true;
                        }
                    }
                    if (na) {
                        newArrangement = true;
                        break;
                    }
                }
                // add it if it is
                if(newArrangement){
                    if(startingGrid[x][y]==2)
                    System.out.println("x: " + x + " y: " + y + "t: \n" + drawArrayWithDirs(arrangement));
                    t.addValidNeighbor(arrangement);
                }
            }
        }
        // done generating the tiles
        tiles = newTiles.toArray(new Tile[0]);
        for (Tile tile : newTiles) {
            System.out.println(tile);
        }
        for (int x = 0; x < xsize; x++) {
            for (int y = 0; y < ysize; y++) {
                setTileAt(x, y, -1);
            }
        }
        boolean done = false;
        int x = 0;
        int y = 0;
        ArrayList<int[][]> badgrids = new ArrayList<>();
        int[][] test = {
            {-1,-1,-1},
            {-1,-1,-1},
            {-1,-1,-1},
        };
        while(!done){
            ArrayList<Integer> canBe = new ArrayList<>();
            if(arrequals(grid, test)){
                System.out.println("AAAAAAAAAAAAAAAAA");
            }
            // find out what the tile can be
            for (int i = 0; i < tiles.length; i++) {
                if(tiles[i].checkPos(x, y)){
                    // if this grid is one we know is allready bad
                    int[][] newGrid = arrclone(grid);
                    setTileAt(x, y, i, newGrid);
                    boolean bad = false;
                    for (int[][] grids : badgrids) {
                        if(arrequals(grids, test)){
                            continue;
                        }
                        if(arrequals(grids, newGrid)){
                            System.out.println(arrToString(grids));
                            bad = true;
                            break;
                        }
                    }
                    // dont add it
                    if(bad) continue;
                    canBe.add(i);
                }
            }
            // if this grid has no solutions say its bad
            if(canBe.size() == 0){
                badgrids.add(arrclone(grid));
                System.out.println(arrToString(arrclone(grid)));
                setTileAt(x, y, -1);
                x--;
                if(x<0){
                    x=xsize-1;
                    y--;
                }
                continue;
            }
            // otherwise set the tile and continue
            setTileAt(x, y, canBe.get(r.nextInt(canBe.size())));
            x++;
            if(x==xsize){
                x=0;
                y++;
            }
            if(x==0&&y==ysize){
                done=true;
            }
            System.out.println(arrToString(grid));
        }
        System.out.println(arrToString(grid));
        WFCrenderer wfcr = new WFCrenderer(new int[][][] {grid, startingGrid});
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
            int validArrangements = validNeighbors.size();
            int[] neighbors = new int[dirs.length];
            for (int i = 0; i < dirs.length; i++) {
                neighbors[i] = getTileAt(x+dirs[i][0], y+ dirs[i][1]);
            }
            for (int[] arrangement : validNeighbors) {
                // check the dirs
                int goodTiles = 0;
                for (int d = 0; d < dirs.length; d++) {
                    // ignore -1
                    if(getTileAt(x+dirs[d][0], y+ dirs[d][1]) == -1 || arrangement[d] == -1 ){
                        goodTiles++;
                        continue;
                    };
                    // if the tile at the dir is not the same as the arrangement say no
                    if(getTileAt(x+dirs[d][0], y+ dirs[d][1]) == arrangement[d]) goodTiles++;
                }
                if(((double)goodTiles)/((double)dirs.length)<1){
                    //System.out.println("x: "+ x + " y: " + y);
                }
                if(((double)goodTiles)/((double)dirs.length)<reqPercent){
                    validArrangements--;
                }
            }
            return validArrangements != 0;
        }

        @Override
        public boolean equals(Object o){
            if(!(o instanceof Tile)) return false;
            return ((Tile)o).type == this.type;
        }

        @Override
        public String toString(){
            String s = "Tile of type: " + type + " \nvalid arr: \n";
            //s += dnut(validNeighbors.toArray(new int[0][0]));
            return s;
        }
    }

    boolean arrequals(int[][] arr1, int[][] arr2){
        if(arr1.length!=arr2.length||arr1[0].length!=arr2[0].length) return false;
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[0].length; j++) {
                if(arr1[i][j]!=arr2[i][j]) return false;
            }
        }
        System.out.println(arrToString(arr1));
        System.out.println("Equals:");
        System.out.println(arrToString(arr2));
        return true;
    }

    int[][] arrclone(int[][] arr1){
        int[][] arr2 = new int[arr1.length][arr1[0].length];
        for (int i = 0; i < arr1.length; i++) {
            for (int j = 0; j < arr1[0].length; j++) {
                arr2[i][j] = arr1[i][j];
            }
        }
        return arr2;
    }

    protected int getTileAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return -1;
        }
        return grid[y][x];
    }

    protected void setTileAt(int x, int y, int val){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return;
        }
        grid[y][x] = val;
    }

    protected void setTileAt(int x, int y, int val, int[][] grid){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return;
        }
        grid[y][x] = val;
    }

    protected int getTileAt(int[][] grid, int x, int y){
        if(x<0||x>=grid[0].length||y<0||y>=grid.length){
            return -2;
        }
        return grid[x][y];
    }

    private String drawArrayWithDirs(int[] a){
        int maxx = -1000;
        int minx = 1000;
        int maxy = -1000;
        int miny = 1000;
        for (int i = 0; i < dirs.length; i++) {
            if(dirs[i][0]>maxx){
                maxx = dirs[i][0];
            }
            if(dirs[i][0]<minx){
                minx = dirs[i][0];
            }
            if(dirs[i][1]>maxy){
                maxy = dirs[i][1];
            }
            if(dirs[i][1]<miny){
                miny = dirs[i][1];
            }
        }
        String[][] strs = new String[maxx-minx+1][maxy-miny+1];
        for (int x = 0; x < strs.length; x++) {
            for (int y = 0; y < strs[x].length; y++) {
                strs[x][y] = " ";
            }
        }
        for (int i = 0; i < dirs.length; i++) {
            strs[dirs[i][1]-minx][dirs[i][1]-miny] = ""+a[i];
        }
        String s = "";
        for (int y = 0; y < strs.length; y++) {
            s += "";
            for (int x = 0; x < strs[0].length; x++) {
                try {if(Integer.valueOf(strs[y][x])>0) s+=" ";} catch (Exception e) {s+=" ";}
                s+= strs[y][x];
            }
            s += "\n";
        }
        s+= "";
        return s;
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
            {1,1,1,1,1,1,1},
            {1,1,2,2,2,1,1},
            {2,2,2,3,2,2,2},
            {1,1,2,2,2,1,1},
            {1,1,1,1,1,1,1},
            {5,5,5,4,5,5,5},
            {1,1,1,1,1,1,1},
        }
        
        ,3, 3, -1);
    }
}