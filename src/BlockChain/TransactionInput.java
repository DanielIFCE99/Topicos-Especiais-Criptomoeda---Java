package BlockChain;

public class TransactionInput { //Recebe os dados de entrada
        public String transactionOutputId; 
	public TransactionOutput UTXO;

        public TransactionInput(String transactionOutputId) {
            this.transactionOutputId = transactionOutputId;
}}