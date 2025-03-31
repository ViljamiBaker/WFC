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
        int[][] currentChanges = new int[ysize][xsize];
        while(!done){
            //if our initial guess was impossible get a new one
            int[] changexyt = findLeastApplicable(findPossibleChanges(startingGrid, new int[][] {}));
            ArrayList<Integer> changePath = new ArrayList<>();
            Change initialChange = new Change(changexyt[0], changexyt[1], changexyt[2]);
            ArrayList<int[]> badChanges = new ArrayList<>();
            Change currentChange = initialChange;
            while (initialChange != null) {
                // apply the changes
                currentChanges = applyChanges(initialChange, changePath.toArray(new Integer[0]));
                // get the best change
                int[] newChangeValues = findLeastApplicable(findPossibleChanges(currentChanges, badChanges.toArray(new int[0][0])));
                // if its null
                if(newChangeValues== null){
                    // dont do it
                    badChanges.add(newChangeValues);
                    continue;
                }
                // otherwise add it to the current path
                changePath.add(currentChange.getChildren().length);
                Change newChange = new Change(newChangeValues[0], newChangeValues[1], newChangeValues[2]);
                currentChange.addChild(newChange);
                currentChange = newChange;
            }
        }
        System.out.println(arrToString(currentChanges));
        WFCrenderer wfcr = new WFCrenderer(new int[][][] {currentChanges, startingGrid});
    }

    private int[][] applyChanges(Change c, Integer[] changePath){
        int[][] grid = new int[ysize][xsize];
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                setTileAt(x, y, -1, grid);
            }
        }
        Change currentChange = c;
        for (int i = 0; i < changePath.length; i++) {
            setTileAt(c.x, c.y, c.to, grid);
            currentChange = currentChange.getChildren()[changePath[i]];
        }
        return grid;
    }

    private boolean[][][] findPossibleChanges(int[][] grid, int[][] exclude){
        boolean[][][] canBe = new boolean[xsize][ysize][tiles.length];
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                if(getTileAt(x, y)!=-1)continue;
                for (int i = 0; i < tiles.length; i++) {
                    // if we already know this one doesnt work then dont do it
                    boolean excludexyt = false;
                    for (int e = 0; e < exclude.length; e++) {
                        if(exclude[e][0]==x&&exclude[e][1]==y&&exclude[e][2]==tiles[i].type){
                            excludexyt = true;
                            break;
                        }
                    }
                    if(excludexyt) continue;
                    canBe[x][y][i] = tiles[i].checkPos(x, y, grid);
                }
            }
        }
        return canBe;
    }

    private int[] findLeastApplicable(boolean[][][] canBe){
        ArrayList<int[]> leastPossiblePoses = new ArrayList<>();
        for (int y = 0; y < ysize; y++) {
            for (int x = 0; x < xsize; x++) {
                for (int i = 0; i < canBe[x][y].length; i++) {
                    if(canBe[x][y][i]){
                        leastPossiblePoses.add(new int[] {x,y,i});
                    }
                }
            }
        }
        // return null if we dont find any
        if(leastPossiblePoses.size()==0){
            return null;
        }
        return leastPossiblePoses.get(r.nextInt(leastPossiblePoses.size()));
    }

    private class Change{
        int x;
        int y;
        int to;
        ArrayList<Change> childChanges = new ArrayList<>();
        boolean goodChange = true;
        public Change(int x, int y, int to){
            this.x = x;
            this.y = y;
            this.to = to;
        }
        public void addChild(Change child){
            childChanges.add(child);
        }
        public Change[] getChildren(){
            return childChanges.toArray(new Change[0]);
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

        public boolean checkPos(int x, int y, int[][] grid){
            //for each of the arrangements
            int validArrangements = validNeighbors.size();
            int[] neighbors = new int[dirs.length];
            for (int i = 0; i < dirs.length; i++) {
                neighbors[i] = getTileAt(grid, x+dirs[i][0], y+ dirs[i][1]);
            }
            for (int[] arrangement : validNeighbors) {
                // check the dirs
                int goodTiles = 0;
                for (int d = 0; d < dirs.length; d++) {
                    // ignore -1
                    if(getTileAt(grid, x+dirs[d][0], y+ dirs[d][1]) == -1 || arrangement[d] == -1 ){
                        goodTiles++;
                        continue;
                    };
                    // if the tile at the dir is not the same as the arrangement say no
                    if(getTileAt(grid, x+dirs[d][0], y+ dirs[d][1]) == arrangement[d]) goodTiles++;
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
    protected void setTileAt(int x, int y, int val, int[][]grid){
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
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
        }
        
        ,4, 4, -1);
    }
}