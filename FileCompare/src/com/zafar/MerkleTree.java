package com.zafar;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.zip.CRC32;

public class MerkleTree {
	private Node root = new Node();
	private int depth = 0;
	private ArrayList<LeafNode> leafQueue;// This is an ordered list having all
											// leaves of the tree from left to
											// right

	public MerkleTree(int d) {
		depth = d;
		leafQueue = new ArrayList<LeafNode>();
	}

	public CRC32 computeCrc(Node n) {
		if (n instanceof LeafNode) {
			return n.getCrc();
		} else {
			CRC32 c1 = computeCrc(n.getLeftChild());
			CRC32 c2 = computeCrc(n.getRightChild());
			CRC32 c3 = n.getCrc();
			byte[] bytes;
			if (c1.getValue() != 0) {
				bytes = ByteBuffer.allocate(8).putLong(c1.getValue()).array();
				c3.update(bytes);
			}
			if (c2.getValue() != 0) {

				bytes = ByteBuffer.allocate(8).putLong(c2.getValue()).array();
				c3.update(bytes);
			}
			return c3;
		}
	}
    /*
    * Create empty complete Binary Tree 
    */
	public void createEmptyTree() {
		createRecursively(root, 1);
	}

	public ArrayList<LeafNode> getLeafQueue() {
		return leafQueue;
	}

	public void createRecursively(Node n, int level) {
		if (level == depth)
			return;
		else {
			if (level < depth - 1) {
				n.setLeftChild(new InternalNode());
				createRecursively(n.getLeftChild(), level + 1);
				n.setRightChild(new InternalNode());
				createRecursively(n.getRightChild(), level + 1);
			} else {

				n.setLeftChild(new LeafNode());
				leafQueue.add((LeafNode) n.getLeftChild());
				n.setRightChild(new LeafNode());
				leafQueue.add((LeafNode) n.getRightChild());
			}
		}
	}

	public Node getRoot() {
		return root;
	}

	public void computeCrc() {
		computeCrc(root);

	}

	public boolean compareTo(MerkleTree tree2, String smallerFileName,
			String biggerFileName) {
		if (root.getCrc().getValue() == tree2.getRoot().getCrc().getValue()) {
			return true;
		} else {
			findDiff(tree2, smallerFileName, biggerFileName);
			return false;
		}
	}

	/*
	 * This is as yet not perfect
	 */
	private void findDiff(MerkleTree other, String smallerFileName,
			String biggerFileName) {
		ArrayList<LeafNode> otherLeaves = other.getLeafQueue();
		for (int i = 0; i < leafQueue.size(); i++) { // leafQueue is lesser or
														// equal in size to
														// otherLeaves
			if(leafQueue.get(i).getStartByteIndex()==0 && leafQueue.get(i).getEndByteIndex()==0)
				break;
			if (leafQueue.get(i).getCrc().getValue() != otherLeaves.get(i)
					.getCrc().getValue()) {
				System.out.println("Problem matching " + smallerFileName
						+ " byte index " + leafQueue.get(i).getStartByteIndex()
						+ " to " + leafQueue.get(i).getEndByteIndex());
			}
		}
	}
	/*
	 * private void findDiff(Node root2, Node root3) { if (root2.getLeftChild()
	 * == null) { System.out.println(((LeafNode) root2).getStartByteIndex() +
	 * " to " + ((LeafNode) root2).getEndByteIndex());
	 * 
	 * return; } if (root2.getLeftChild().getCrc().getValue() !=
	 * root3.getLeftChild() .getCrc().getValue()) {
	 * findDiff(root2.getLeftChild(), root3.getLeftChild()); } if
	 * (root2.getRightChild().getCrc().getValue() != root3.getRightChild()
	 * .getCrc().getValue()) { findDiff(root2.getRightChild(),
	 * root3.getRightChild()); }
	 * 
	 * }
	 */
}
