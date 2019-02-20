package blockchain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
	
	public String transactionId; // Sadrzi hash transakcije
	public PublicKey sender; // Pošiljalac adresa
	public PublicKey reciepient; // Primalac adresa
	public float value; // Koliko zeli da posalje
	public byte[] signature; // Sprecavanje da neko drugi moze da pristupi nasim sredstvima

	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

	private static int sequence = 0; // Koliko transakcija je obavljeno.

	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.reciepient = to;
		this.value = value;
		this.inputs = inputs;
	}

	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASig(privateKey, data);
	}

	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}

	public boolean processTransaction() {

		if (verifySignature() == false) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}

		for (TransactionInput i : inputs) {
			i.UTXO = Blockchain.UTXOs.get(i.transactionOutputId);
		}

		// Proverava da li je transakcije validna
		if (getInputsValue() < Blockchain.minimumTransaction) {
			System.out.println("Transaction Inputs too small: " + getInputsValue());
			System.out.println("Please enter the amount greater than " + Blockchain.minimumTransaction);
			return false;
		}

		// Generise transakcioni izlaz
		float leftOver = getInputsValue() - value; // koliko nam je ostalo nakon transakcije
		transactionId = calulateHash();
		outputs.add(new TransactionOutput(this.reciepient, value, transactionId)); // slanje tranksacije primaocu
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId)); // slanje ostatka posiljaocu

		// dodaje ostatak na ostale bitcoin koji poseduje posiljalac
		for (TransactionOutput o : outputs) {
			Blockchain.UTXOs.put(o.id, o);
		}

		// obrise koliko je potroseno
		for (TransactionInput i : inputs) {
			if (i.UTXO == null)
				continue; // ako transakcije ne postoji nastavlja dalje
			Blockchain.UTXOs.remove(i.UTXO.id);
		}

		return true;
	}

	public float getInputsValue() {
		float total = 0;
		for (TransactionInput i : inputs) {
			if (i.UTXO == null)
				continue; // ako transakcije nema nastavi dalje
			total += i.UTXO.value;
		}
		return total;
	}

	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}

	private String calulateHash() {
		sequence++; // dovecava se da nebi dve iste transakcije imale isti hash
		return StringUtil.applySha256(StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(reciepient)
				+ Float.toString(value) + sequence);
	}
	
}
