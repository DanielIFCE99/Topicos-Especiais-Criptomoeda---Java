package BlockChain;
import java.util.ArrayList;
import com.google.gson.GsonBuilder;

public class noobchain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static int difficulty = 0;
    
    public static void main(String[] args) {
        blockchain.add(new Block("Ola, sou seu primeiro block", "0"));
        System.out.println("Tentando minerar bloco 1\n");
        blockchain.get(0).mineBlock(difficulty);
        
        blockchain.add(new Block("Ola, sou seu segundo block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Tentando minerar o bloco 2\n");
        blockchain.get(1).mineBlock(difficulty);
        
        blockchain.add(new Block("Ola, sou seu terceiro block", blockchain.get(blockchain.size() - 1).hash));
        System.out.println("Tentando minerar o bloco 3\n");
        blockchain.get(2).mineBlock(difficulty);
        
        String blockchainJason = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
        System.out.println("\nO block chain");
        System.out.println(blockchainJason);
        
    }     
        
        
    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0','0');
        
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
            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("Os blocos nao foram minerado");
                return false;
            }
        }
        
        return true;         
        
    }
}
