package BlockChain;

import java.security.Security;
import java.util.ArrayList;
//import java.util.Base64;
//import com.google.gson.GsonBuilder;

public class noobchain {
	
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;

	public static void main(String[] args) {	
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		
		walletA = new Wallet();
		walletB = new Wallet();
		
		System.out.println("Chaves publicas e privadas:");
		System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
		System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
		 
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		
		System.out.println("Assinatura Verificada");
		System.out.println(transaction.verifiySignature());
		
	}
}