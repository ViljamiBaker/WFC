package wfc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class WFCMaker {
    // the grid of ints to output
    int[][] grid;
    // directions that are concidered neighbors
    static int[][] dirs = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};

    WFCMaker.Tile[] tiles;

    Random r = new Random();

    double reqPercent = 8.0/8.0;

    int xsize;

    int ysize;

    public WFCMaker(int[][] startingGrid, int xsize, int ysize){
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
                    System.out.println("x: " + x + " y: " + y + "t: \n" + dogunut(arrangement));
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

        while(!done){
            // loop over all cells and find what they could be
            boolean[][][] canBe = new boolean[xsize][ysize][tiles.length];
            for (int y = 0; y < ysize; y++) {
                for (int x = 0; x < xsize; x++) {
                    if(getTileAt(x, y)!=-1)continue;
                    for (int i = 0; i < tiles.length; i++) {
                        canBe[x][y][i] = tiles[i].checkPos(x, y);
                    }
                }
            }
            // find the one with the least possiblities
            boolean foundLeast = false;
            // leastPossiblilitiesTiles
            ArrayList<int[]> LPT = new ArrayList<>();

            int leastPossiblilities = Integer.MAX_VALUE-1;
            // untill we found the least possiblities
            while (!foundLeast) {
                // loop over all of the cells
                boolean bad = false;
                for (int y = 0; y < ysize; y++) {
                    for (int x = 0; x < xsize; x++) {
                        if(getTileAt(x, y) != -1) continue;
                        // loop over all of the possibilites
                        int possibilityCount = 0;
                        for (int i = 0; i < canBe[x][y].length; i++) {
                            if(canBe[x][y][i]){
                                possibilityCount++;
                            }
                        }
                        // if we found the new least change it and continue the while loop
                        //if(possibilityCount<leastPossiblilities&&possibilityCount!=0){
                        //    LPT.clear();
                        //    leastPossiblilities = possibilityCount;
                        //    bad = true;
                        //    break;
                        //}
                        // if we found one with the same ammount add it to the list
                        if(possibilityCount!=0){
                            LPT.add(new int[] {x,y});
                        }
                    }
                    if (bad) {
                        break;
                    }
                }
                if (bad) {
                    continue;
                }

                foundLeast = true;
            }
            if (LPT.size()==0) {
                System.out.println("LPT No possiblities found :((((");
                for (int x = 0; x < xsize; x++) {
                    for (int y = 0; y < ysize; y++) {
                        setTileAt(x, y, -1);
                    }
                }
                continue;
            }

            // set one of the least possiblities to a possiblitiy

            int randindex = r.nextInt(LPT.size());

            ArrayList<Integer> possiblities = new ArrayList<>();

            for (int i = 0; i < canBe[LPT.get(randindex)[0]][LPT.get(randindex)[1]].length; i++) {
                if(canBe[LPT.get(randindex)[0]][LPT.get(randindex)[1]][i]){
                    possiblities.add(tiles[i].type);
                }
            }
            int randindex2 = r.nextInt(possiblities.size());
            
            setTileAt(LPT.get(randindex)[0], LPT.get(randindex)[1], possiblities.get(randindex2));
            // final check
            done = true;
            for (int y = 0; y < ysize; y++) {
                for (int x = 0; x < xsize; x++) {
                    if(getTileAt(x, y) == -1)
                    done = false;
                }
            }
        }
        System.out.println(arrToString(grid));
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
            s += dnut(validNeighbors.toArray(new int[0][0]));
            return s;
        }
    }

    protected int getTileAt(int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return -2;
        }
        return grid[y][x];
    }

    protected void setTileAt(int x, int y, int val){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return;
        }
        grid[y][x] = val;
    }

    protected int getTileAt(int[][] grid, int x, int y){
        if(x<0||x>=xsize||y<0||y>=ysize){
            return -2;
        }
        return grid[x][y];
    }

    private String dnut(int[][] a){
        String s = "";
        for (int[] is : a) {
            s+=dogunut(is) + "\n";
        }
        return s;
    }

    private String dogunut(int[] a){
        return a[3] + ", " + a[2] + ", " + a[1] + "\n" + a[4] + ",  , " + a[0] + "\n" + a[5] + ", " + a[6] + ", " + a[7];
    }

    private boolean arrequals(int[] arr1, int[] arr2){
        int[] offsets = {0,2,4,6};
        boolean[] good = {false,false,false,false};
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 8; j++) {
                if(arr1[j]!=arr2[(j+offsets[i])%8]){
                    good[i] = false;
                }
            }
        }
        return good[0]||good[1]||good[2]||good[3];
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
            {1,1,2,3,2,1,1},
            {2,2,2,2,2,2,2},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
            {1,1,1,1,1,1,1},
        }
        
        ,7, 7);
    }
}