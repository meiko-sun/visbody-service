package cn.lazy.visbody.enums;

public enum UserSexEnum {
    MEN("男", 1), WOMEN("女", 2);

    private String name;
    private int index;

    private UserSexEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static String getName(int index) {
        for (UserSexEnum c : UserSexEnum.values()) {
            if (c.getIndex() == index) {
                return c.name;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
}
