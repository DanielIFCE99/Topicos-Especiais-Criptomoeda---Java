package BlockChain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

//import java.util.Base64;
//import com.google.gson.GsonBuilder;

public class JavCripty {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
        public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>();
	public static int difficulty = 5;
        public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	public static Wallet walletB;
        public static Transaction genesisTransaction;


	public static void main(String[] args) {	
		Scanner tec = new Scanner(System.in);
                
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		
		walletA = new Wallet();
		walletB = new Wallet();
		Wallet coinbase = new Wallet();

		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
		genesisTransaction.generateSignature(coinbase.privateKey);	 	
		genesisTransaction.transactionId = "0"; 
		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value, genesisTransaction.transactionId)); //manually add the Transactions Output
		UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0)); 
		
		System.out.println("Criando e minerando o bloco genesis(Primeiro Bloco)...)");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		
                //testing
                System.out.println("O saldo da Carteira A é: " + walletA.getBalance());
		Block block1 = new Block(genesis.hash);
		System.out.println("\nO saldo da carteira A é: " + walletA.getBalance());
                System.out.println("Digite o valor da transferencia da carteira A para a Carteira B: ");
                int wA =  tec.nextInt();
		System.out.println("\nA carteira A está tentando transferir" + wA + "JavCripts para a caeteira B");
		block1.addTransaction(walletA.sendFunds(walletB.publicKey, wA));
		addBlock(block1);
		System.out.println("\nO saldo da carteira A é: " + walletA.getBalance());
		System.out.println("O saldo da carteira B é: " + walletB.getBalance());
		
		Block block2 = new Block(block1.hash);
		System.out.println("Digite o valor da nova transferencia: ");
                wA =  tec.nextInt();
                System.out.println("\nA carteira A está tentando transferir" + wA + "JavCripts para a caeteira B");                
		block2.addTransaction(walletA.sendFunds(walletB.publicKey, wA));
		addBlock(block2);
		System.out.println("\nO saldo da carteira A é: " + walletA.getBalance());
		System.out.println("O saldo da carteira B: " + walletB.getBalance());
		
		Block block3 = new Block(block2.hash);
                System.out.println("Digite o valor da nova transferencia: ");
                wA =  tec.nextInt();
		System.out.println("\nA carteira B está tentando transferir" + wA + "JavCripts para a caeteira A");
		block3.addTransaction(walletB.sendFunds( walletA.publicKey, wA));
		System.out.println("\nO saldo da carteira A é: " + walletA.getBalance());
		System.out.println("O saldo da carteira B é: " + walletB.getBalance());
		
		
		isChainValid();
		
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("Hashes atuais não são iguais");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("Hashes anteriores não iguais");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("Este bloco não foi minerado");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.transactions.size(); t++) {
				Transaction currentTransaction = currentBlock.transactions.get(t);
				
				if(!currentTransaction.verifiySignature()) {
					System.out.println("Assinatura na transação(" + t + ") é invalido");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("As entradas são iguais às saídas na transação(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("Entrada referenciada na transação(" + t + ") está desaparecido");
						return false;
					}
					
					if(input.UTXO.value != tempOutput.value) {
						System.out.println("Transação de entrada referenciada(" + t + ") valor invalido");
						return false;
					}
					
					tempUTXOs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.id, output);
				}
				
				if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
					System.out.println("Transação(" + t + ") o destinatário da saída não é quem deveria ser");
					return false;
				}
				if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
					System.out.println("Transação(" + t + ") saída 'mudança' não é remetente.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain é válido");
		return true;
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}
}