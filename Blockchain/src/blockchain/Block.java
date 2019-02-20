package blockchain;

import java.util.ArrayList;
import java.util.Date;

import blockchain.StringUtil;
import blockchain.Transaction;

public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public long timeStamp;
	public int nonce;

	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash(); 
	}

	// Izracunava hash na osnovu kontentka koji block ima
	public String calculateHash() {
		String calculatedhash = StringUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return calculatedhash;
	}
	//mine
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty); // Create a string with difficulty * "0"
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Block Mined!!! : " + hash);
	}

	// Dodavanje transakcije ovom bloku
	public boolean addTransaction(Transaction transaction) {
		// process transaction and check if valid, unless block is genesis block then
		// ignore.
		if (transaction == null)
			return false;
		if ((!"0".equals(previousHash))) {
			if ((transaction.processTransaction() != true)) {
				System.out.println("Transakciju nije moguce izvrsiti. Odbacena.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transakcija je uspesno dodata u blok!");
		return true;
	}

}
