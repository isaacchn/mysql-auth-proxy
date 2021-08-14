package me.isaac.audit.protocol_v2.convertor.rule;

public abstract class AbsRule implements IRule {
    private String field;//字段名
    private byte[] value;//字节数组值

    public AbsRule(String field) {
        this.field = field;
    }
}
