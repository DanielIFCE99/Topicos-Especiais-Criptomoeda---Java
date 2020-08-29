package BlockChain;
import static BlockChain.noobchain.blockchain;
import java.util.Date;

    public class Block{

              public String hash;
              public String previousHash;
              private String data;
              private long timeStamp;
              public static int difficulty = 0;
               private int nonce;

       public Block(String data, String previosHash){
            this.data = data;
           this.previousHash = previosHash;
           this.timeStamp = new Date().getTime();
           this.hash = calculHash();
       }

       public String calculHash(){
           String calculatedhash = StringUtil.applySha256(
                   previousHash +
                   Long.toString(timeStamp) +
                   data
           );
          return calculatedhash;
       }

       public void mineBlock(int difficulty){
           String target = new String(new char[difficulty]).replace('\0','0');
           while(!hash.substring(0, difficulty).equals(target)){
               nonce ++;
               hash = calculHash();
           }
           System.out.println("Bloco minerado!!! : " + hash);
       }

       public static Boolean isChainValid(){
           Block currentBlock;
           Block previousBlock;

           for (int i = 1; i < blockchain.size(); i++) {
               currentBlock = blockchain.get(i);
               previousBlock = blockchain.get(i-1);

               if (!currentBlock.hash.equals(currentBlock.calculHash())) {
                   System.out.println("Hashs atuais nao iguais");
                           return false;
               }
               if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                   System.out.println("Hashs anteriores nao iguais");
                           return false;
               }
           }

           return true;
       }
   }
