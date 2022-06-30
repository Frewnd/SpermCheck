package tools;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrangeArray {
    //获取最小值
    public static int getMinIndex(double[] arr) {
        int minIndex=0;//假设第一个元素为最大值 那么下标设为0
        for(int i =0;i<arr.length-1;i++){
            if(arr[minIndex]>arr[i+1]){
                minIndex=i+1;
            }
        }
        return  minIndex;
    }
    //获取最大值
    public static int getMaxIndex(double[] arr) {
        int minIndex=0;//假设第一个元素为最大值 那么下标设为0
        for(int i =0;i<arr.length-1;i++){
            if(arr[minIndex]<arr[i+1]){
                minIndex=i+1;
            }
        }
        return  minIndex;
    }
    //给定一个长度为n的数组，寻找其中最大的k个数
    public static ArrayList<Double> findKthElements(double[] arr1, int k) {

       double[] arr = Arrays.copyOf(arr1,arr1.length);
        ArrayList<Double> res = new ArrayList<Double>();
        if(arr.length <= 0 || arr == null || arr.length < k) {
            return res;
        }

        Arrays.sort(arr);
        for(int i = arr.length - 1;i > arr.length - 1 - k;i --) {
            res.add(arr[i]);
        }
        return res;
    }
    //数组去0
    public static double[] deleteZero(double[] oldArr){
        int h = 0;
        for (double b : oldArr) {
            // 判断，如果oldArr数组的值不为0那么h就加1
            if (b != 0) {
                h++;
            }
        }
        // 得到了数组里不为0的个数，以此个数定义一个新数组，长度就是h
        double newArr[] = new double[h];
        // 这里偷个懒，不想从新定义增量了，所以把增量的值改为0
        h = 0;
        // 在次循环读取oldArr数组的值
        for (double c : oldArr) {
            // 把不为0的值写入到newArr数组里面
            if (c != 0) {
                newArr[h] = (int) c;
                h++;// h作为newArr数组的下标，没写如一个值，下标h加1
            }
        }
        return newArr;
    }
    //求平均值
    public static int averageArr(int [] arr){
        int sum = 0;
        for(int b : arr){
            sum = sum + b;
        }
        int a = sum/arr.length;
        return a;
    }
    public static double averageArr(double [] arr){
        double sum = 0;
        for(double b : arr){
            sum = sum + b;
        }
        double a = sum/arr.length;
        return a;
    }
}
