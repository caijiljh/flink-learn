/**
 * @Author jinhong.liu
 * @Description
 * @Date 2021/8/11
 */

class A implements  aa{
    public void fun() {
        print();
    }
    private void print() {
        System.out.println("A类print()方法");
    }

    @Override
    public  void a() {
        System.out.println("a");

    }
}

class B extends A{
    public void a() {
        System.out.println("B类中的print()方法");
    }
}


class Demo04 {
    public static void main(String[] args) {
        new B().a();
    }
}