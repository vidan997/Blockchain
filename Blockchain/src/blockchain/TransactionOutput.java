package blockchain;
import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient; //ko prima bitcoin
	public float value; //koliko primalac poseduje bitcoina
	public String parentTransactionId; //id transakcije
	
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//provera da li transakcije pripada vama
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
	
	
}
