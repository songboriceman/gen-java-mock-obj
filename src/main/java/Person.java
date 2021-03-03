import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class Person {
    private Long id;
    private String name;
    private Date birth;
    private List<Set<Address>> addressList;
    private Favor[] favors;
    private List<Order> orderList;
    private Set<Map<String,List<Set<Favor>>>> favorSet;


    @Data
    public static class Favor{
        private String title;
        private String describe;
        private double score;
    }

    @Data
    public static class Address{
        private String country;
        private String province;
        private String city;
        private String street;
        private String contract;
        private String phone;
        private Boolean isDefault;
    }

    @Data
    public static class Order{
        private Double totalPrice;
        private Address address;
        private Map<Long,List<Favor>> goodsFavor;
        private List<SubOrder> subOrders;
    }

    @Data
    public static class SubOrder{
        private Long   goodId;
        private Double salePrice;
        private Double originalPrice;
        private float benefit;
        private String   title;
    }

}