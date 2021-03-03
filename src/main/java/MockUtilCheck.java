import com.alibaba.fastjson.JSONObject;


public class MockUtilCheck {

    public static void  main(String[] args){


        Person person  = MockUtil.mock(Person.class);
        System.out.println(JSONObject.toJSONString(person,true));
    }
}