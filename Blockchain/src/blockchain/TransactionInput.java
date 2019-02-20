package blockchain;

public class TransactionInput {
	public String transactionOutputId; //Odnosi se na koju izlaznu transakciju
	public TransactionOutput UTXO;
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}	
}
