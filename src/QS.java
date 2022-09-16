public class QS {

    private int [] data;
    public QS(int [] arr){
        data = arr;
    }

    private int partition(int left, int right){
        int key = data[left];
        int l = left;
        int r = right;
        while (l < r){
            while (l < r && data[r] >= key)
                r--;
            if(l < r) {
                data[l] = data[r];
                l++;
                while(data[l] < key && l < r)
                    l++;
                if(l < r)
                {
                    data[r] = data[l];
                    r--;
                }
            }
        }
        data[l] = key;
        return l;
    }

    public void sort(int left, int right){
        if (left < right){
            int q = partition(left, right);
            sort(left, q - 1);
            sort(q + 1, right);
        }
    }

}