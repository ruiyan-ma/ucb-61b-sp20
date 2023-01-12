package timing;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GrowList {
	public abstract void add(int e);

	public abstract GrowList newList();

	/**
	 * Conduct a timing experiment by inserting MAXSIZE
	 * elements into NLISTS different lists. The output
	 * of this will be a list such that ith item is the
	 * time for the ith insertion. We use NLISTS different
	 * lists and average the performance over all of them
	 * to hopefully reduce noise in our data collection.
	 */
	public List<Double> score(int maxSize, int nLists) {
		Timer t = new Timer();
		Random random = new Random();
		List<Double> scores = new ArrayList<>();
		List<GrowList> lists = new ArrayList<>();

		for (int i = 0; i < nLists; i++) {
			lists.add(newList());
		}

		for (int count = 0; count < maxSize; count += 1) {
			int e = random.nextInt();
			t.start();
			for (GrowList list : lists) {
				list.add(e);
			}
			double elapsedMs = t.stop();
			scores.add(elapsedMs / nLists);
		}
		return scores;
	}

	/**
	 * Conduct a timing experiments as above. The output of this
	 * will be a list such that ith item is the cumulative total
	 * of the average times of the first i insertions.
	 */
	public List<Double> accumScore(int maxSize, int nLists) {
		List<Double> scores = score(maxSize, nLists);
		double accumTotal = 0;
		for (int i = 0; i < scores.size(); i += 1) {
			accumTotal += scores.get(i);
			scores.set(i, accumTotal);
		}
		return scores;
	}
}

class GeomGrowList extends GrowList {
	/**
	 * Array insertion. Θ(N) for N insertions.
	 */
	public GeomGrowList() {
		_arr = new int[8];
		_size = 0;
	}

	@Override
	public void add(int e) {
		if (_size == _arr.length) {
			int[] newArr = new int[_size * 2];
			System.arraycopy(_arr, 0, newArr, 0, _size);
			_arr = newArr;
		}
		_arr[_size] = e;
		_size += 1;
	}

	@Override
	public GrowList newList() {
		return new GeomGrowList();
	}

	private int[] _arr;
	private int _size;
}

class ArithGrowList extends GrowList {
	/**
	 * Array insertion with resizing the array by increasing
	 * the size of the array by one when the array is fully
	 * filled up. Θ(N^2) for N insertions.
	 */
	public ArithGrowList() {
		_arr = new int[8];
		_size = 0;
	}

	@Override
	public void add(int e) {
		if (_size == _arr.length) {
			int[] newArr = new int[_size + 1];
			System.arraycopy(_arr, 0, newArr, 0, _size);
			_arr = newArr;
		}
		_arr[_size] = e;
		_size += 1;
	}

	@Override
	public GrowList newList() {
		return new ArithGrowList();
	}

	private int[] _arr;
	private int _size;
}

class JavaGrowList extends GrowList {

	public JavaGrowList() {
		_list = new ArrayList<>();
	}

	@Override
	public void add(int e) {
		_list.add(e);
	}

	@Override
	public GrowList newList() {
		return new GeomGrowList();
	}

	private ArrayList<Integer> _list;
}
