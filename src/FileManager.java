
import java.io.*;
import java.util.Iterator;
import java.util.Set;

public class FileManager{
   public final int  FAILCODE = -1;
   private PlaceManager pm;
   private  FileReader readFil;
   private  BufferedReader buffFil;
   private FileWriter writeFil;
   private  PrintWriter printFil;

   FileManager(PlaceManager pm){
       this.pm = pm;

   }

   public int loadPlaces(String filename){
        try{
           readFil = new FileReader(filename);
           buffFil = new BufferedReader(readFil);
            String line;
            while((line = buffFil.readLine()) != null ){
                String [] parts = line.split(",");
                Category category = Category.valueOf(parts[1]);
                int x = Integer.parseInt(parts[2]);
                int y = Integer.parseInt(parts[3]);
                Position position = new Position(x,y);
                if(parts[0].equals("Named")){
                    String name = parts[4];
                    pm.addPlace(new NamedPlace(position,category,name,pm));
                }if(parts[0].equals("Described")){
                    String name = parts[4];
                    String description = parts[5].replace("COMMA",",");
                    pm.addPlace(new DescribedPlace(position,category,name, description,pm));
                }
            }
            buffFil.close();
            readFil.close();
            return 0;
        }catch(IOException fe){
            return FAILCODE;
        }catch(ArrayIndexOutOfBoundsException ae){
            return FAILCODE;
        }
   }

   public int savePlaces(String filename, Set<Place> places){
       try{
           writeFil = new FileWriter(filename);
           printFil = new PrintWriter(writeFil);
           Iterator iter = places.iterator();
           while( iter.hasNext() ){
               try{
                 String type = "Named";
                 Place p = (Place)iter.next();
                 int x = p.getPosition().getX();
                 int y = p.getPosition().getY();
                 String description = "";
                 if(p instanceof DescribedPlace){
                     type = "Described";
                     description = ((DescribedPlace) p).getDescription().replace(",","COMMA");
                 }
                     printFil.println(type+","+p.getCategory().getText()+","+x+","+y+","+p.getName()+","+description);

               }catch(NullPointerException ne){
                   return FAILCODE;
               }
           }
           writeFil.close();
           printFil.close();
           return 0;
       }catch(IOException fe){
           return FAILCODE;
       }
   }

}
