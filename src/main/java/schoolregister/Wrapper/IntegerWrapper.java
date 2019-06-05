package schoolregister.Wrapper;

public class IntegerWrapper {
    private Integer value;

    public IntegerWrapper(Integer value){
        this.value = value;
    }
    public IntegerWrapper(){
        value = 0;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
