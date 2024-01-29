import java.util.Scanner;
import java.util.Arrays;

public class TerminalMinesweeper{
  
  public static void main(String[] args){
    //creates a 2D array (the grid)
    Scanner input = new Scanner(System.in);
    System.out.println("Welcome to Minesweeper. Input the number of rows");
    int r = input.nextInt();
    System.out.println("Input the number of columns");
    int c = input.nextInt();
    String[][] gridhidden = new String[r][c]; //shows where mines are but are hidden to player. temp tho
    String[][] gridshown = new String[r][c]; //this is the grid that the player sees
    String[][] gridhiddenwithoutbombs = new String[r][c]; //this is the grid that is compared to gridshown to see if you won
    //tells us how many mines there will be (20%)
    int mines = (int)(.2*r*c);
    //calls the method to generate grid with mines and number boxes onto gridhidden
    gridhidden=generateField(gridhidden,mines);
    //prints gridshown
    for(int y=0; y<gridshown.length; y++){
      for(int z=0; z<gridshown[y].length; z++){
        if(gridshown[y][z] == null){
          gridshown[y][z] = "-";
        }
      }
    }
    printgrid(gridshown);
    //creates gridhiddenwithflags
    for(int a=0; a<gridhidden.length;a++){
      for(int b=0; b<gridhidden[a].length;b++){
        if (gridhidden[a][b]!="*"){
          gridhiddenwithoutbombs[a][b]=gridhidden[a][b];
          if(gridhidden[a][b]=="0"){
          gridhiddenwithoutbombs[a][b]+="";
          }
        }
        else{
          gridhiddenwithoutbombs[a][b]="f";
        }
      }
    }
    //calls the method to start player interactions
    while (gridshown != gridhiddenwithoutbombs){
      System.out.println("? if unsure, f to flag, c to choose");
      String answer = input.next();
      if(answer.equals("c")){
        System.out.println("Row: ");
        int row = input.nextInt();
        System.out.println("Column: ");
        int column = input.nextInt();
        row-=1;
        column-=1;
        //if hit a mine, gridhidden is given, showing mines and hits (emptyBox and numBox)
        if(gridhidden[row][column]=="*"){
          System.out.println("You lose!");
          printgrid(gridhidden);
          break;
        }
        //else, gridshown is given, showing just hits 
        else if (gridhidden[row][column]!="0"){
          gridshown[row][column]=gridhidden[row][column];
          printgrid(gridshown);
          if (Arrays.deepEquals(gridshown, gridhiddenwithoutbombs)){
          System.out.print("YOU WIN!");
          break;
          }
        }
        else{
          gridshown[row][column]=gridhidden[row][column];
          printgrid(surroundZeroes(showZeroes(gridshown, gridhidden, row, column), gridhidden));
          if (Arrays.deepEquals(gridshown, gridhiddenwithoutbombs)){
          System.out.print("YOU WIN!");
          break;
          }
        }
      }
      else if (answer.equals("f")){
        System.out.println("Row: ");
        int rOw = input.nextInt();
        System.out.println("Column: ");
        int cOl = input.nextInt();
        rOw-=1;
        cOl-=1;
        gridshown[rOw][cOl] = "f";
        printgrid(gridshown);
        if(gridshown[rOw][cOl] != gridhiddenwithoutbombs[rOw][cOl]){
        System.out.println("You lose!");
        printgrid(gridhidden);
        break;
        }
        if (Arrays.deepEquals(gridshown, gridhiddenwithoutbombs)){
          System.out.print("YOU WIN!");
          break;
          }
      }
      else if (answer.equals("?")){
        System.out.println("Row: ");
        int rOW = input.nextInt();
        System.out.println("Column: ");
        int cOL = input.nextInt();
        rOW-=1;
        cOL-=1;
        gridshown[rOW][cOL] = "?";
        printgrid(gridshown);
      }
    }
  }
  
  //prints the grid
  public static void printgrid(String array[][]){
    for (int r = 0; r<array.length; r++){
      for(int c = 0; c<array[r].length; c++){
      System.out.print(array[r][c] + " ");
      }
      System.out.println();
    }
  }
  
  //the method for generating mines and numberBoxes on gridhidden.
  public static String[][] generateField(String[][] array, int mines){
    //generates mines
    int count = 0;
    while(count<mines){
      int a = (int)(Math.random() * array.length);
      int b = (int)(Math.random() * array[0].length);
      array[a][b] = "*";
      count++;
    }
    //makes the rest of the boxes into 0
    for(int r=0; r<array.length; r++){
      for(int c=0; c<array[r].length; c++){
        if(array[r][c] == null){
          array[r][c] = "0";
        }
      }
    }
    //adds the number boxes around each mine *rips hair out*
    for(int r=0; r<array.length; r++){
      for(int c=0; c<array[r].length; c++){
        //middle boxes
        if(array[r][c]=="*" && r-1>-1 && r+1<array.length && c+1<array[0].length && c-1>-1){
          if (array[r-1][c-1] != "*"){
            array[r-1][c-1]=Integer.toString(Integer.parseInt(array[r-1][c-1])+1);}
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r-1][c+1] != "*"){
            array[r-1][c+1]=Integer.toString(Integer.parseInt(array[r-1][c+1])+1);}
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
          if (array[r+1][c-1] != "*"){
            array[r+1][c-1]=Integer.toString(Integer.parseInt(array[r+1][c-1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
          if (array[r+1][c+1] != "*"){
            array[r+1][c+1]=Integer.toString(Integer.parseInt(array[r+1][c+1])+1);}
        }
        //corner boxes
        else if(array[r][c]=="*" && r-1==-1 && c-1==-1 && r+1<array.length && c+1<array[0].length){
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
          if (array[r+1][c+1] != "*"){
            array[r+1][c+1]=Integer.toString(Integer.parseInt(array[r+1][c+1])+1);}
        }
        else if(array[r][c]=="*" && r-1==-1 && c-1>-1 && r+1<array.length && c+1==array[0].length){
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
          if (array[r+1][c-1] != "*"){
            array[r+1][c-1]=Integer.toString(Integer.parseInt(array[r+1][c-1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
        }
        else if(array[r][c]=="*" && r-1>-1 && c-1==-1 && r+1==array.length && c+1<array[0].length){
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r-1][c+1] != "*"){
            array[r-1][c+1]=Integer.toString(Integer.parseInt(array[r-1][c+1])+1);}
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
        }
        else if(array[r][c]=="*" && r-1>-1 && c-1>-1 && r+1==array.length && c+1==array[0].length){
          if (array[r-1][c-1] != "*"){
            array[r-1][c-1]=Integer.toString(Integer.parseInt(array[r-1][c-1])+1);}
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
        }
        //side boxes
        else if(array[r][c]=="*" && r-1==-1 && c-1>-1 && r+1<array.length && c+1<array[0].length){
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
          if (array[r+1][c-1] != "*"){
            array[r+1][c-1]=Integer.toString(Integer.parseInt(array[r+1][c-1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
          if (array[r+1][c+1] != "*"){
            array[r+1][c+1]=Integer.toString(Integer.parseInt(array[r+1][c+1])+1);}
        }
        else if(array[r][c]=="*" && r-1>-1 && c-1==-1 && r+1<array.length && c+1<array[0].length){
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r-1][c+1] != "*"){
            array[r-1][c+1]=Integer.toString(Integer.parseInt(array[r-1][c+1])+1);}
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
          if (array[r+1][c+1] != "*"){
            array[r+1][c+1]=Integer.toString(Integer.parseInt(array[r+1][c+1])+1);}
        }
        else if(array[r][c]=="*" && r-1>-1 && c-1>-1 && r+1==array.length && c+1<array[0].length){
          if (array[r-1][c-1] != "*"){
            array[r-1][c-1]=Integer.toString(Integer.parseInt(array[r-1][c-1])+1);}
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r-1][c+1] != "*"){
            array[r-1][c+1]=Integer.toString(Integer.parseInt(array[r-1][c+1])+1);}
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
          if (array[r][c+1] != "*"){
            array[r][c+1]=Integer.toString(Integer.parseInt(array[r][c+1])+1);}
        }
        else if(array[r][c]=="*" && r-1>-1 && c-1>-1 && r+1<array.length && c+1==array[0].length){
          if (array[r-1][c-1] != "*"){
            array[r-1][c-1]=Integer.toString(Integer.parseInt(array[r-1][c-1])+1);}
          if (array[r-1][c] != "*"){
            array[r-1][c]=Integer.toString(Integer.parseInt(array[r-1][c])+1);}
          if (array[r][c-1] != "*"){
            array[r][c-1]=Integer.toString(Integer.parseInt(array[r][c-1])+1);}
          if (array[r+1][c-1] != "*"){
            array[r+1][c-1]=Integer.toString(Integer.parseInt(array[r+1][c-1])+1);}
          if (array[r+1][c] != "*"){
            array[r+1][c]=Integer.toString(Integer.parseInt(array[r+1][c])+1);}
        }
      }
    }
    return array;
  }
  
  //when the player puts in coordinates that hit a 0, this method shows the zeroes around it *rips hair out again
  public static String[][] showZeroes(String[][] array, String[][] hiddenarray, int r, int c){
    while(hiddenarray[r][c]=="0"){
      array[r][c]=hiddenarray[r][c];
      hiddenarray[r][c]+="";
      //middle
      if(r-1>-1 && r+1<array.length && c+1<array[0].length && c-1>-1){
        showZeroes(array, hiddenarray, r+1, c-1);
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r+1, c+1);
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r, c+1);
        showZeroes(array, hiddenarray, r-1, c-1);
        showZeroes(array, hiddenarray, r-1, c);
        showZeroes(array, hiddenarray, r-1, c+1);
      }
      //corners
      else if(r-1==-1 && c-1==-1 && r+1<array.length && c+1<array[0].length){
        showZeroes(array, hiddenarray, r, c+1);
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r+1, c+1);
      }
      else if(r-1==-1 && c-1>-1 && r+1<array.length && c+1==array[0].length){
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r+1, c-1);
      }
      else if(r-1>-1 && c-1==-1 && r+1==array.length && c+1<array[0].length){
        showZeroes(array, hiddenarray, r-1, c+1);
        showZeroes(array, hiddenarray, r, c+1);
        showZeroes(array, hiddenarray, r-1, c);
      }
      else if(r-1>-1 && c-1>-1 && r+1==array.length && c+1==array[0].length){
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r-1, c-1);
        showZeroes(array, hiddenarray, r-1, c);
      }
      //side boxes
      else if(r-1==-1 && c-1>-1 && r+1<array.length && c+1<array[0].length){
        showZeroes(array, hiddenarray, r+1, c-1);
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r+1, c+1);
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r, c+1);
      }
      else if(r-1>-1 && c-1==-1 && r+1<array.length && c+1<array[0].length){
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r+1, c+1);
        showZeroes(array, hiddenarray, r, c+1);
        showZeroes(array, hiddenarray, r-1, c);
        showZeroes(array, hiddenarray, r-1, c+1);
      }
      else if(r-1>-1 && c-1>-1 && r+1==array.length && c+1<array[0].length){
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r, c+1);
        showZeroes(array, hiddenarray, r-1, c-1);
        showZeroes(array, hiddenarray, r-1, c);
        showZeroes(array, hiddenarray, r-1, c+1);
      }
      else if(r-1>-1 && c-1>-1 && r+1<array.length && c+1==array[0].length){
        showZeroes(array, hiddenarray, r+1, c-1);
        showZeroes(array, hiddenarray, r+1, c);
        showZeroes(array, hiddenarray, r, c-1);
        showZeroes(array, hiddenarray, r-1, c-1);
        showZeroes(array, hiddenarray, r-1, c);
      }
    }
   return array;
  }
  
  //this method shows the numbers around the zero
  public static String[][] surroundZeroes(String[][] arrayed, String[][] hiddenarray){
    for(int r=0; r<arrayed.length; r++){
      for(int c=0; c<arrayed[r].length; c++){
        //middle boxes
        if(arrayed[r][c]=="0" && r-1>-1 && r+1<arrayed.length && c+1<arrayed[0].length && c-1>-1){
          if (arrayed[r-1][c-1] != "0"){
            arrayed[r-1][c-1]=hiddenarray[r-1][c-1];}
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r-1][c+1] != "0"){
            arrayed[r-1][c+1]=hiddenarray[r-1][c+1];}
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
          if (arrayed[r+1][c-1] != "0"){
            arrayed[r+1][c-1]=hiddenarray[r+1][c-1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
          if (arrayed[r+1][c+1] != "0"){
            arrayed[r+1][c+1]=hiddenarray[r+1][c+1];}
        }
        //corner boxes
        else if(arrayed[r][c]=="0" && r-1==-1 && c-1==-1 && r+1<arrayed.length && c+1<arrayed[0].length){
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
          if (arrayed[r+1][c+1] != "0"){
            arrayed[r+1][c+1]=hiddenarray[r+1][c+1];}
        }
        else if(arrayed[r][c]=="0" && r-1==-1 && c-1>-1 && r+1<arrayed.length && c+1==arrayed[0].length){
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
          if (arrayed[r+1][c-1] != "0"){
            arrayed[r+1][c-1]=hiddenarray[r+1][c-1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
        }
        else if(arrayed[r][c]=="0" && r-1>-1 && c-1==-1 && r+1==arrayed.length && c+1<arrayed[0].length){
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r-1][c+1] != "0"){
            arrayed[r-1][c+1]=hiddenarray[r-1][c+1];}
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
        }
        else if(arrayed[r][c]=="0" && r-1>-1 && c-1>-1 && r+1==arrayed.length && c+1==arrayed[0].length){
          if (arrayed[r-1][c-1] != "0"){
            arrayed[r-1][c-1]=hiddenarray[r-1][c-1];}
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
        }
        //side boxes
        else if(arrayed[r][c]=="0" && r-1==-1 && c-1>-1 && r+1<arrayed.length && c+1<arrayed[0].length){
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
          if (arrayed[r+1][c-1] != "0"){
            arrayed[r+1][c-1]=hiddenarray[r+1][c-1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
          if (arrayed[r+1][c+1] != "0"){
            arrayed[r+1][c+1]=hiddenarray[r+1][c+1];}
        }
        else if(arrayed[r][c]=="0" && r-1>-1 && c-1==-1 && r+1<arrayed.length && c+1<arrayed[0].length){
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r-1][c+1] != "0"){
            arrayed[r-1][c+1]=hiddenarray[r-1][c+1];}
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
          if (arrayed[r+1][c+1] != "0"){
            arrayed[r+1][c+1]=hiddenarray[r+1][c+1];}
        }
        else if(arrayed[r][c]=="0" && r-1>-1 && c-1>-1 && r+1==arrayed.length && c+1<arrayed[0].length){
          if (arrayed[r-1][c-1] != "0"){
            arrayed[r-1][c-1]=hiddenarray[r-1][c-1];}
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r-1][c+1] != "0"){
            arrayed[r-1][c+1]=hiddenarray[r-1][c+1];}
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
          if (arrayed[r][c+1] != "0"){
            arrayed[r][c+1]=hiddenarray[r][c+1];}
        }
        else if(arrayed[r][c]=="0" && r-1>-1 && c-1>-1 && r+1<arrayed.length && c+1==arrayed[0].length){
          if (arrayed[r-1][c-1] != "0"){
            arrayed[r-1][c-1]=hiddenarray[r-1][c-1];}
          if (arrayed[r-1][c] != "0"){
            arrayed[r-1][c]=hiddenarray[r-1][c];}
          if (arrayed[r][c-1] != "0"){
            arrayed[r][c-1]=hiddenarray[r][c-1];}
          if (arrayed[r+1][c-1] != "0"){
            arrayed[r+1][c-1]=hiddenarray[r+1][c-1];}
          if (arrayed[r+1][c] != "0"){
            arrayed[r+1][c]=hiddenarray[r+1][c];}
        }
      }
    }
    return arrayed;
  }
}
