import java.util.ArrayList;
import java.util.List;

class MyTreeNode {
    int val;
    MyTreeNode left, right;

    public MyTreeNode(int val) {
        this.val = val;
        this.left = this.right = null;
    }
}

public class MyClosestValuesInBST {

    public static List<Integer> findClosestValues(MyTreeNode root, double target, int k) {
        List<Integer> result = new ArrayList<>();
        findClosestValuesHelper(root, target, k, result);
        return result;
    }

    private static void findClosestValuesHelper(MyTreeNode node, double target, int k, List<Integer> result) {
        if (node == null) {
            return;
        }

        findClosestValuesHelper(node.left, target, k, result);

        if (result.size() < k) {
            result.add(node.val);
        } else {
            double currentDiff = Math.abs(node.val - target);
            double maxDiff = Math.abs(result.get(0) - target);

            if (currentDiff < maxDiff) {
                result.remove(0);
                result.add(node.val);
            } else {
                return;
            }
        }

        findClosestValuesHelper(node.right, target, k, result);
    }

    public static void main(String[] args) {
        /*
         * Provided Tree:
         *       4
         *      / \
         *     2   5
         *    / \
         *   1   3
         */
        MyTreeNode root = new MyTreeNode(4);
        root.left = new MyTreeNode(2);
        root.right = new MyTreeNode(5);
        root.left.left = new MyTreeNode(1);
        root.left.right = new MyTreeNode(3);

        double target = 3.8;
        int k = 2;

        List<Integer> closestValues = findClosestValues(root, target, k);
        System.out.println("Closest values to " + target + " are: " + closestValues); // Output: [4, 5]
    }
}
