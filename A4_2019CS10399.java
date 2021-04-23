import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class A4_2019CS10399 {
	private static class Node implements Comparable<Node> {
		String label;
		Long weight;

		private Node(String label) {
			this.label = label;
		}

		@Override
		public String toString() {
			return label;
		}

		@Override
		public boolean equals(Object other) {
			return other != null && getClass().equals(other.getClass()) && label.equals(((Node) other).label)
					&& weight == ((Node) other).weight;
		}

		@Override
		public int compareTo(Node other) {
			if (equals(other))
				return 0;
			int compare = weight.compareTo(other.weight);
			if (compare != 0)
				return -compare;
			return -label.compareTo(other.label);
		}

		@Override
		public int hashCode() {
			return label.hashCode();
		}
	}

	private static class ArrayListComparable<T extends Comparable<T>> extends ArrayList<T>
			implements Comparable<ArrayListComparable<T>> {

		@Override
		public boolean equals(Object o) {
			return super.equals(o);
		}

		@Override
		public int compareTo(ArrayListComparable<T> other) {
			if (equals(other))
				return 0;
			if (size() != other.size())
				return size() > other.size() ? -1 : 1;
			for (int i = 0; i < size(); ++i) {
				int compare = get(i).compareTo(other.get(i));
				if (compare != 0)
					return -compare;
			}
			return 0;
		}

		@Override
		public int hashCode() {
			return super.hashCode();
		}

		private static final long serialVersionUID = 1L;

	}

	private static ArrayList<Node> nodesList;
	private static HashMap<String, Integer> invNodesMap;
	private static ArrayList<HashMap<String, Long>> adjList;

	private static <T extends Comparable<T>> void sort(ArrayList<T> arrayList) {
		for (int sz = 1; sz < arrayList.size(); sz *= 2) {
			int l = 0;
			while (l < arrayList.size())
				l = merge(arrayList, l, sz);
		}
	}

	private static <T extends Comparable<T>> int merge(ArrayList<T> arrayList, int l, int sz) {
		ArrayList<T> copy1List = new ArrayList<>(), copy2List = new ArrayList<>();
		int i = l, j = 0, r = Math.min(l + sz, arrayList.size() - 1);
		while (i < r)
			copy1List.add(arrayList.get(i++));
		r = Math.min(l + 2 * sz, arrayList.size());
		while (i < r)
			copy2List.add(arrayList.get(i++));
		i = 0;
		while (i < copy1List.size() && j < copy2List.size()) {
			if (copy1List.get(i).compareTo(copy2List.get(j)) <= 0)
				arrayList.set(l++, copy1List.get(i++));
			else
				arrayList.set(l++, copy2List.get(j++));
		}
		while (i < copy1List.size())
			arrayList.set(l++, copy1List.get(i++));
		while (j < copy2List.size())
			arrayList.set(l++, copy2List.get(j++));

		return l;
	}

	private static String[] split(String string) {
		ArrayList<Integer> indices = new ArrayList<>();
		indices.add(-1);
		int quoteCount = 0;
		for (int i = 0; i < string.length(); ++i) {
			if (string.charAt(i) == '\"' && (i == 0 || i == string.length() - 1 || string.charAt(i - 1) == ','
					|| string.charAt(i + 1) == ','))
				++quoteCount;
			else if (string.charAt(i) == ',') {
				if (quoteCount % 2 == 1)
					continue;
				indices.add(i);
			}
		}
		indices.add(string.length());
		String[] ret = new String[indices.size() - 1];
		for (int i = 0; i < indices.size() - 1; ++i) {
			String subString = string.substring(indices.get(i) + 1, indices.get(i + 1));
			if (subString.length() > 1 && subString.charAt(0) == '\"'
					&& subString.charAt(subString.length() - 1) == '\"')
				subString = subString.substring(1, subString.length() - 1);
			ret[i] = subString;
		}
		return ret;
	}

	private static void extractNodes(String file) throws IOException {
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			int i = 0;
			String line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] character = split(line);
				// ensure that the line has correct number of elements
				if (character.length != 2)
					throw new IOException();
				// check if already added
				if (invNodesMap.containsKey(character[1]))
					continue;
				nodesList.add(new Node(character[1]));
				invNodesMap.put(character[1], i++);
			}
		}
	}

	private static void extractEdges(String file) throws IOException {
		// initialise the adj list
		for (int i = 0; i < nodesList.size(); ++i)
			adjList.add(new HashMap<>());
		try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
			String line = bufferedReader.readLine();
			while ((line = bufferedReader.readLine()) != null) {
				String[] stw = split(line);
				// ensure that the line has correct number of elements
				if (stw.length != 3)
					throw new IOException();
				long weight;
				try {
					weight = Long.parseLong(stw[2]);
				} catch (NumberFormatException e) {
					throw new IOException(e.getMessage());
				}
				// check if already present (then ensure weight is same)
				if (adjList.get(invNodesMap.get(stw[0])).get(stw[1]) != null) {
					if (adjList.get(invNodesMap.get(stw[0])).get(stw[1]) != weight)
						throw new IOException();
					continue;
				}
				adjList.get(invNodesMap.get(stw[0])).put(stw[1], weight);
				adjList.get(invNodesMap.get(stw[1])).put(stw[0], weight);
			}
		}
	}

	private static void updateWeights() {
		for (int i = 0; i < adjList.size(); ++i) {
			long weight = 0;
			for (long w : adjList.get(i).values())
				weight += w;
			nodesList.get(i).weight = weight;
		}
	}

	private static void dfs(int u, ArrayListComparable<String> componentList, ArrayList<Boolean> visitedList) {
		visitedList.set(u, true);
		componentList.add(nodesList.get(u).label);
		for (String string : adjList.get(u).keySet()) {
			int v = invNodesMap.get(string);
			if (Boolean.FALSE.equals(visitedList.get(v)))
				dfs(v, componentList, visitedList);
		}
	}

	private static void average() {
		long sum = 0;
		for (HashMap<String, Long> hashMap : adjList)
			sum += hashMap.size();
		double avg = sum * 1.0 / nodesList.size();
		if (Double.isNaN(avg))
			avg = 0;
		System.out.printf("%.2f%n", avg);
	}

	private static void rank() {
		updateWeights();
		ArrayList<Node> copyNodesList = new ArrayList<>(nodesList);
		sort(copyNodesList);
		for (int i = 0; i < copyNodesList.size() - 1; ++i)
			System.out.print(copyNodesList.get(i).label + ",");
		if (!copyNodesList.isEmpty())
			System.out.print(copyNodesList.get(copyNodesList.size() - 1).label + '\n');
	}

	private static void independent_storylines_dfs() {
		ArrayList<Boolean> visitedList = new ArrayList<>();
		for (int i = 0; i < nodesList.size(); ++i)
			visitedList.add(false);
		ArrayList<ArrayListComparable<String>> componentLists = new ArrayList<>();
		for (int i = 0; i < adjList.size(); ++i) {
			if (Boolean.FALSE.equals(visitedList.get(i))) {
				componentLists.add(new ArrayListComparable<>());
				dfs(i, componentLists.get(componentLists.size() - 1), visitedList);
			}
		}
		for (ArrayListComparable<String> arrayListComparable : componentLists) {
			sort(arrayListComparable);
			for (int i = 0; i < (arrayListComparable.size() + 1) / 2; ++i) {
				String temp = arrayListComparable.get(arrayListComparable.size() - i - 1);
				arrayListComparable.set(arrayListComparable.size() - i - 1, arrayListComparable.get(i));
				arrayListComparable.set(i, temp);
			}
		}
		sort(componentLists);
		for (ArrayListComparable<String> arrayListComparable : componentLists) {
			for (int i = 0; i < arrayListComparable.size() - 1; ++i)
				System.out.print(arrayListComparable.get(i) + ",");
			System.out.print(arrayListComparable.get(arrayListComparable.size() - 1) + '\n');
		}
	}

	public static void main(String[] args) {
		long time = System.nanoTime();
		nodesList = new ArrayList<>();
		invNodesMap = new HashMap<>();
		adjList = new ArrayList<>();
		// check if the arguments supplied are in correct format
		if (args.length != 3) {
			System.err.println(
					"Insufficient or too many arguments, expected format is:\n<nodes csv file> <edges csv file> <function: average | rank | independent_storylines_dfs>");
			return;
		}
		try {
			System.err.println("Extracting nodes...");
			extractNodes(args[0]);
			System.err.println("Success!");
		} catch (IOException e) {
			System.err.println("File address mentioned incorrect or error in reading nodes file\nAborting...");
			return;
		}
		try {
			System.err.println("Extracting edges...");
			extractEdges(args[1]);
			System.err.println("Success!");
		} catch (IOException e) {
			System.err.println("File address mentioned incorrect or error in reading edges file\nAborting...");
			return;
		}
		switch (args[2]) {
			case "average":
				average();
				break;
			case "rank":
				rank();
				break;
			case "independent_storylines_dfs":
				independent_storylines_dfs();
				break;
			default:
				System.err.println(
						"Incorrect function command. Expected one of: average | rank | independent_storylines_dfs\nAborting...");
				return;
		}
		System.err.println(args[2] + " execution completed!");
		System.err.println((System.nanoTime() - time) / 1e9);
	}
}