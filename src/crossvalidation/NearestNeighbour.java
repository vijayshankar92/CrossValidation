package crossvalidation;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

public class NearestNeighbour implements Comparable {
	int distance;
	int index;
	int x;
	int y;
	String sign;

	public NearestNeighbour(int dist, int sampleIndex) {
		super();
		this.distance = dist;
		this.index = sampleIndex;
	}

	public NearestNeighbour(int x, int y, String sign) {

		this.x = x;
		this.y = y;
		this.sign = sign;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int dist) {
		this.distance = dist;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int sampleIndex) {
		this.index = sampleIndex;
	}

	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		NearestNeighbour distance = (NearestNeighbour) o;
		if (this.distance > distance.distance) {
			return 1;
		} else if (this.distance < distance.distance) {
			return -1;
		} else {
			return 0;
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public static void main(String[] args) {
		File file1 = new File("input1.txt");
		File file2 = new File("input2.txt");
		File output = new File("output.txt");
		try {
			Scanner scanner1 = new Scanner(file1.getAbsoluteFile());
			Scanner scanner2 = new Scanner(file2.getAbsoluteFile());
			FileWriter fileWriter = new FileWriter(output);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            
			int numberFolds = Integer.parseInt(scanner1.next());
			int numberExamples = Integer.parseInt(scanner1.next());
			int numberRuns = Integer.parseInt(scanner1.next());
			int count = 0;
			StringBuffer finalString=new StringBuffer();
			ArrayList<ArrayList<Integer>> permutation = new ArrayList<ArrayList<Integer>>(
					numberRuns);
			HashMap<Integer, NearestNeighbour> dataSample = new HashMap<Integer, NearestNeighbour>();
			HashMap<Integer, NearestNeighbour> resultData = new HashMap<Integer, NearestNeighbour>();
			int rows = Integer.parseInt(scanner2.next());
			int columns = Integer.parseInt(scanner2.next());
			String[][] data = new String[rows][columns];
			int runCount = 0;
			while (runCount < numberRuns) {
				ArrayList<Integer> tempPerm = new ArrayList<Integer>();
				count = 0;
				while (count < numberExamples) {
					tempPerm.add(Integer.parseInt(scanner1.next()));
					count++;
				}
				permutation.add(tempPerm);
				runCount++;
			}
			int sampleCount = 0;
			int dataSampleCounter = 0;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					String temp = scanner2.next();
					data[i][j] = temp;
					resultData.put(sampleCount, new NearestNeighbour(i, j, temp));
					sampleCount++;
					if (temp.equals("+") || temp.equals("-")) {
						dataSample.put(dataSampleCounter, new NearestNeighbour(i, j,
								temp));
						dataSampleCounter++;
					}
				}
			}
			ArrayList<HashMap<Integer, ArrayList<NearestNeighbour>>> distance = new ArrayList<HashMap<Integer, ArrayList<NearestNeighbour>>>(
					numberRuns);
			for (int i = 0; i < numberRuns; i++) {
				distance.add(calculateDistance(permutation.get(i), dataSample));
			}
			HashMap<Integer, ArrayList<NearestNeighbour>> minDistMapdot = new HashMap<Integer, ArrayList<NearestNeighbour>>();
			minDistMapdot = dotNeighbour(resultData, dataSample);
			for (int nn = 1; nn < 5; nn++) {
				double error = 0, variance = 0, sigma = 0;
				double error1 = 0, error2 = 0, error3 = 0;
				double result = 0;
				ArrayList<Double> errors = new ArrayList<Double>();
				for (int i = 0; i < numberRuns; i++) {
					errors.add(calculateError(permutation.get(i), dataSample,
							distance.get(i), nn));
				}
				for (int i = 0; i < numberRuns; i++) {
					result = result + errors.get(i);
				}
				result = result / numberRuns;
				for (int i = 0; i < numberRuns; i++) {
					variance = variance + Math.pow(errors.get(i) - result, 2);
				}
				variance = variance / (numberRuns - 1);

				sigma = Math.sqrt(variance);
				System.out.println(nn + "NN");
                System.out.println("Error = " + result);
				System.out.println("Sigma = " + sigma);
				finalString.append(nn + "NN");
				finalString.append("\nError = " + result);
				finalString.append("\nSigma = " + sigma+"\n");
				System.out.println();

			}
			for (int nn = 1; nn < 6; nn++) {
				System.out.println(nn + "NN");
				finalString.append("\n"+nn + "NN\n");
				int modCount = 0;
				for (int i = 0; i < resultData.size(); i++)
				{
					modCount++;
					String sign = resultData.get(i).getSign();
					if (sign.equals(".")) {
						String sign1 = calculateNeighbour(i, minDistMapdot, nn,
								dataSample);
						System.out.print(sign1 + "\t");
						finalString.append(sign1 + "\t");
					} else {
						System.out.print(sign + "\t");
						finalString.append(sign + "\t");
					}
					if ((modCount % 5) == 0) {
						System.out.println();
						finalString.append("\n");
					}
				}
				System.out.println();
				 
			}
			 try {
	        	    try {
	        	    	printWriter.write(new String(finalString));
	        	    } finally {
	        	    	printWriter.close();
	        	    }
	        	} finally {
	        	   fileWriter.close();
	        	}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String calculateNeighbour(int i,
			HashMap<Integer, ArrayList<NearestNeighbour>> minDist, int nn,
			HashMap<Integer, NearestNeighbour> data) {
		int positive = 0;
		int negative = 0;
		ArrayList<NearestNeighbour> arrDis = minDist.get(i);
		String check;
		String checksign;
		for (int j = 0; j < nn; j++) {
			check = data.get(arrDis.get(j).getIndex()).getSign();
			if (check.equals("+")) {
				positive++;
			} else if (check.equals("-")) {
				negative++;
			}
		}
		if (positive > negative) {
			checksign = "+";
		} else if (negative > positive) {
			checksign = "-";
		} else {
			checksign = "-";
		}
		return checksign;
	}

	public static HashMap<Integer, ArrayList<NearestNeighbour>> calculateDistance(
			ArrayList<Integer> perm, HashMap<Integer, NearestNeighbour> dataSample) {
		HashMap<Integer, ArrayList<NearestNeighbour>> minDistMap = new HashMap<Integer, ArrayList<NearestNeighbour>>();

		int count1 = perm.size() / 2;
		int count2 = perm.size() - count1;
		for (int i = 0; i < count1; i++) {
			int temporary = perm.get(i);
			int x1 = dataSample.get(temporary).getX();
			int y1 = dataSample.get(temporary).getY();
			String sign1 = dataSample.get(temporary).getSign();
			int min = 4000;
			ArrayList<NearestNeighbour> distanceList = new ArrayList<NearestNeighbour>();
			int dist;
			for (int j = count1; j < perm.size(); j++) {
				int x2 = dataSample.get(perm.get(j)).getX();
				int y2 = dataSample.get(perm.get(j)).getY();
				String sign2 = dataSample.get(perm.get(j)).getSign();
				// distance.add((int) (Math.pow((x2-x1), 2)+Math.pow((y2-y1),
				// 2)));
				dist = ((int) (Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)));
				distanceList.add(new NearestNeighbour(dist, perm.get(j)));
			}
			Collections.sort(distanceList);
			minDistMap.put(temporary, distanceList);

		}
		for (int i = count1; i < perm.size(); i++) {
			int temporary = perm.get(i);
			int x1 = dataSample.get(temporary).getX();
			int y1 = dataSample.get(temporary).getY();
			String sign1 = dataSample.get(temporary).getSign();
			int min = 4000;

			ArrayList<NearestNeighbour> distanceList = new ArrayList<NearestNeighbour>();
			int dist;
			for (int j = 0; j < count1; j++) {

				int x2 = dataSample.get(perm.get(j)).getX();
				int y2 = dataSample.get(perm.get(j)).getY();
				String sign2 = dataSample.get(perm.get(j)).getSign();
				// distance.add((int) (Math.pow((x2-x1), 2)+Math.pow((y2-y1),
				// 2)));
				dist = ((int) (Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2)));
				distanceList.add(new NearestNeighbour(dist, perm.get(j)));

			}
			Collections.sort(distanceList);
			minDistMap.put(temporary, distanceList);
		}
		return minDistMap;
	}

	@SuppressWarnings("unchecked")
	public static HashMap<Integer, ArrayList<NearestNeighbour>> dotNeighbour(
			HashMap<Integer, NearestNeighbour> data, HashMap<Integer, NearestNeighbour> dataSample) {
		HashMap<Integer, ArrayList<NearestNeighbour>> minDistMap = new HashMap<Integer, ArrayList<NearestNeighbour>>();

		for (int i = 0; i < data.size(); i++)

		{
			String sign = data.get(i).getSign();
			if (sign.equals(".")) {
				int x1 = data.get(i).getX();
				int y1 = data.get(i).getY();
				ArrayList<NearestNeighbour> distanceList = new ArrayList<NearestNeighbour>();
				for (int j = 0; j < dataSample.size(); j++) {
					int dist;
					int x2 = dataSample.get(j).getX();
					int y2 = dataSample.get(j).getY();
					dist = ((int) (Math.pow((x2 - x1), 2) + Math.pow((y2 - y1),
							2)));
					distanceList.add(new NearestNeighbour(dist, j));
					Collections.sort(distanceList);
					minDistMap.put(i, distanceList);
				}
			}
		}
		return minDistMap;
	}

	public static double calculateError(ArrayList<Integer> perm,
			HashMap<Integer, NearestNeighbour> dataSample,
			HashMap<Integer, ArrayList<NearestNeighbour>> minDist, int nn) {
		double error = 0;

		for (int i = 0; i < perm.size(); i++) {
			String sign1 = calculateNeighbour(perm.get(i), minDist, nn,
					dataSample);
			String sign2 = dataSample.get(perm.get(i)).getSign();
			if (!(sign1.equals(sign2))) {
				error++;
			}

		}
		error = error / perm.size();
		return error;
	}
}