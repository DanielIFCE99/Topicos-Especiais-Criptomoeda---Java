package BlockChain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;


public class Transaction {
    
        public String transactionId; //hash da transação
	public PublicKey sender; //endereço do remetente / chave publica
	public PublicKey reciepient; // endereço do receptor / chave publico
	public float value; // valor da transferencia 
	public byte[] signature;  // garante a integridade 
	
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>(); // entradas de transação
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>(); //saidas de transação
	
	private static int sequence = 0; // Aproximação da quantia de transações geradas
	
	
	public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	
	private String calulateHash() {
		sequence++; 
		return StringUtil.applySha256(
				StringUtil.getStringFromKey(sender) +
				StringUtil.getStringFromKey(reciepient) +
				Float.toString(value) + sequence
				);
	}
       
        public void generateSignature(PrivateKey privateKey) {
	String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
	signature = StringUtil.applyECDSASig(privateKey,data);		
}
        
        public boolean verifiySignature() {
                String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient) + Float.toString(value)	;
                return StringUtil.verifyECDSASig(sender, data, signature);
        }
        
        
        public boolean processTransaction() {//Verifica se as transações são validas 
		
		if(verifiySignature() == false) {
			System.out.println("Falha ao verificar a assinatura da transação, assinatura invalida!");
			return false;
		}
				
		//reúna entradas de transação (certifique-se de que não sejam gastas)
		for(TransactionInput i : inputs) {
			i.UTXO = JavCripty.UTXOs.get(i.transactionOutputId);
		}

		//verifique se a transação é válida:
		if(getInputsValue() < JavCripty.minimumTransaction) {
			System.out.println("Valor minimo de transação não atingido: " + getInputsValue());
			return false;
		}
		
		//Gera as saidas das transações:
		float leftOver = getInputsValue() - value; //obter o valor das entradas:
		transactionId = calulateHash();
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId)); //enviar valor desejado ao destinatário
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId)); //Envia o valor restante ao remetente		
				
		//Adciona o valor transferido aos não gastos(Futuro saldo do remetente)
		for(TransactionOutput o : outputs) {
			JavCripty.UTXOs.put(o.id , o);
		}
		
		//Remove do saldo do remetente o valor transferido:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //Ignora a transação se ela for negada 
			JavCripty.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
//retorna os valores das estradas
	public float getInputsValue() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //Ignora a transação se ela for negada  
			total += i.UTXO.value;
		}
		return total;
	}

//retorna os valores das saidas:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
