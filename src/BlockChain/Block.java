package BlockChain;
import static BlockChain.JavCripty.blockchain;
import java.util.ArrayList;
import java.util.Date;

    public class Block{ //Criação do bloco
                
              public String hash;
              public String previousHash;
              private String merkleRoot;
              public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //os dados serão mensagens simples
              private long timeStamp;
              private int nonce;

       public Block(String previousHash ) { //Construtor
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash(); 
	}

       public String calculateHash() { //Calcula as Hashs
		String calculatedhash = StringUtil.applySha256( 
				previousHash +              // hash do bloco anterior
				Long.toString(timeStamp) + // o horario da transação
				Integer.toString(nonce) + // basicamente o Id da tansação
				merkleRoot //contém informações sobre cada hash de transação que já esteve em um bloco específico em um blockchain.
				);
		return calculatedhash; //retorna a hash que foi calculada
	}

       public void mineBlock(int difficulty) { // Mineração do bloco
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty); //Criando uma String com dificuldade 0 
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Bloco minerado com sucesso!!! :\n o Hash do bloco é: " + hash+"\n");
	}

       
      public boolean addTransaction(Transaction transaction) {
		//processar a transação e verifica se é válida.
		if(transaction == null) return false;		
		if((!"0".equals(previousHash))) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Pedido de transação negado.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Pedido de transação aceito. A transferencia será adicionada no proximo bloco.");
		return true;
	}
	
}
       /*
           *public static Boolean isChainValid(){
           Block currentBlock;
           Block previousBlock;

           for (int i = 1; i < blockchain.size(); i++) {
               currentBlock = blockchain.get(i);
               previousBlock = blockchain.get(i-1);

               if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
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
   }*/
