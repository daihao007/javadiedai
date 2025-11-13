package src;

public class Course {
    private final String id;
    private final String name;
    private final String teacherId;
    private final int day; // 1-7
    private final int start; // 1-14
    private final int end;   // 1-14
    private final double credit; // 0..12, 保留一位小数打印时处理
    private final int period; // (0, 1280]

    public Course(String id, String name, String teacherId, int day, int start, int end, double credit, int period) {
        this.id = id;
        this.name = name;
        this.teacherId = teacherId;
        this.day = day;
        this.start = start;
        this.end = end;
        this.credit = credit;
        this.period = period;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getTeacherId() { return teacherId; }
    public int getDay() { return day; }
    public int getStart() { return start; }
    public int getEnd() { return end; }
    public double getCredit() { return credit; }
    public int getPeriod() { return period; }
}
