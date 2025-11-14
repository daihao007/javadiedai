import src.Course;
import src.Role;
import src.User;

import java.util.*;
import java.text.DecimalFormat;
import java.util.zip.CheckedOutputStream;

public class Test {
    private static final Map<String,User> users=new HashMap<>();
    private static final Map<String, Course> courses=new HashMap<>();
    private static final Map<String,Set<String>> studentCourses=new HashMap<>();
    private static final Map<String,Set<String>> courseStudents=new HashMap<>();
    private static final int COURSE_CAPACITY=30;
    private static  int courseCounter=0;
    public static void main(String[] args) {
        // 定义已知命令及其参数数量范围
        Map<String, int[]> commandArgsRange = new HashMap<>();
        commandArgsRange.put("start", new int[]{0, 2}); // 最少0个参数，最多2个参数
        commandArgsRange.put("restart", new int[]{1, 1}); // 恰好1个参数
        commandArgsRange.put("quit", new int[]{0, 0}); // 不接受参数
        commandArgsRange.put("register",new int[]{5,5});
        commandArgsRange.put("login",new int[]{2,2});
        commandArgsRange.put("logout",new int[]{0,1});
        commandArgsRange.put("printInfo",new int[]{0,1});
        commandArgsRange.put("createCourse",new int[]{4,4});
        commandArgsRange.put("listCourse",new int[]{0,1});
        commandArgsRange.put("selectCourse",new int[]{1,1});
        commandArgsRange.put("cancelCourse",new int[]{1,1});


        List<String> loggedInUsers = new ArrayList<>();
        String currentUser=null;

        Scanner scanner = new Scanner(System.in);
        //System.out.println("请输入命令和参数：");

        while (true) {
            // 等待用户输入
            String input = scanner.nextLine().trim();
            if(input.isEmpty()){
                continue;
            }
            String[] parts = input.split("\\s+");

            if (parts.length > 0) {
                // 第一个字符串为命令
                String command = parts[0];
                if (!commandArgsRange.containsKey(command)) {
                    System.out.println("Command '" + command + "' not found");
                    continue;
                }

                // 检查参数数量是否合法
                int[] range = commandArgsRange.get(command);
                int argCount = parts.length - 1; // 参数数量
                if (argCount < range[0] || argCount > range[1]) {
                    System.out.println("Illegal argument count");
                    continue;
                }
                //处理register命令
                if("register".equals(command)){
                    //参数顺序：学工号、姓名、密码、确认密码、申请身份
                    String id=parts[1];
                    String name=parts[2];
                    String password=parts[3];
                    String confirm=parts[4];
                    String roleRaw=parts[5];
                    Role role=parseRole(roleRaw);
                    if(role==null){
                        System.out.println("Illegal identity");
                        continue;
                    }
                    if(!isValidIdForRole(id,role)){
                        System.out.println("Illegal user id");
                        continue;
                    }
                    if(users.containsKey(id)){
                        System.out.println("User id exists");
                        continue;
                    }
                    if(!isValidName(name)){
                        System.out.println("Illegal user name");
                        continue;
                    }
                    //密码合法性检测
                    if(!isValidPassword(password)){
                        System.out.println("Illegal password");
                        continue;
                    }
                    if(!password.equals(confirm)){
                        System.out.println("Passwords do not match");
                        continue;
                    }
                    User u=new User(id,name,password,role);
                    users.put(id,u);
                    System.out.println("Register success");
                    continue;
                }
                // 处理 quit 命令
                if ("quit".equals(command)) {
                    for (String user : loggedInUsers) {
                        System.out.println(user + " Bye~");
                    }
                    System.out.println("----- Good Bye! -----");
                    System.exit(0);
                }
                if("login".equals(command)){
                    String id=parts[1];
                    String password=parts[2];
                    if(!isValidIdGeneral(id)){
                        System.out.println("Illegal user id");
                        continue;
                    }
                    if(!users.containsKey((id))){
                        System.out.println("User does not exist");
                        continue;
                    }
                    if(loggedInUsers.contains((id))){
                        System.out.println(id+" is online");
                        continue;
                    }
                    User u=users.get(id);
                    if(u.getPassword()==null|| !u.getPassword().equals(password)){
                        System.out.println("Wrong password");
                        continue;
                    }
                    loggedInUsers.add(id);
                    currentUser=id;
                    System.out.println("Welcome to ACP, "+ id);
                    continue;
                }

                if("logout".equals(command)){
                    if(argCount==0){
                        //无参数：当前用户退出
                        if(currentUser==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        loggedInUsers.remove(currentUser);
                        System.out.println(currentUser + " Bye~");
                        if(!loggedInUsers.isEmpty()){
                            currentUser=loggedInUsers.get(loggedInUsers.size()-1);
                        }
                        else{
                            currentUser=null;
                        }
                        continue;
                    }
                    else{
                        String target =parts[1];
                        if(currentUser==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        User cur =users.get(currentUser);
                        if(cur==null||cur.getRole()!=Role.ADMIN){
                            System.out.println("Permission denied");
                            continue;
                        }
                        if(!isValidIdGeneral(target)){
                            System.out.println("Illegal user id");
                            continue;
                        }
                        if(!users.containsKey(target)){
                            System.out.println("User does not exist");
                            continue;
                        }
                        if(!loggedInUsers.contains(target)){
                            System.out.println(target+" is not online");
                            continue;
                        }
                        loggedInUsers.remove((target));
                        System.out.println(target +" Bye~");
                        if(target.equals(currentUser)){
                            if(!loggedInUsers.isEmpty()){
                                currentUser=loggedInUsers.get(loggedInUsers.size()-1);
                            }
                            else{
                                currentUser=null;
                            }
                        }
                        continue;
                    }
                }

                if("printInfo".equals(command)){
                    if(argCount==0){
                        if(currentUser==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        User u=users.get(currentUser);
                        if(u==null){
                            System.out.println("Users does not exist");
                            continue;
                        }
                        printUserInfo(u);
                        System.out.println("Print information success");
                        continue;
                    }
                    else{
                        String target=parts[1];
                        if(currentUser==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        User cur =users.get(currentUser);
                        if(cur==null||cur.getRole()!=Role.ADMIN){
                            System.out.println("Perssion denied");
                            continue;
                        }
                        if(!isValidIdGeneral(target)){
                            System.out.println("User does not exist");
                            continue;
                        }
                        User targetUser=users.get(target);
                        printUserInfo(targetUser);
                        System.out.println("Print information success");
                        continue;
                    }
                }

                if("createCourse".equals(command)){
                    if(currentUser==null){
                        System.out.println("No one is online");
                        continue;
                    }
                    User curUser=users.get(currentUser);
                    if(curUser==null||curUser.getRole()!=Role.TEACHER){
                        System.out.println("Permission denied");
                        continue;
                    }
                    long teacherCourseCount = courses.values().stream().filter(c->curUser.getId().equals((c.getTeacherId()))).count();
                    if(teacherCourseCount>=10){
                        System.out.println("Course count reaches limit");
                        continue;
                    }
                    String courseName=parts[1];
                    if(courseName==null|| !courseName.matches("^[A-Za-z][A-Za-z0-9_-]{0,19}$")){
                        System.out.println("Illegal course name");
                        continue;
                    }
                    boolean nameExists =courses.values().stream().anyMatch(c->curUser.getId().equals(c.getTeacherId())&& courseName.equals(c.getName()));
                    if(nameExists){
                        System.out.println("Course name exists");
                        continue;
                    }
                    //课程时间解析
                    String timeRaw=parts[2];
                    int day,start,end;
                    try {
                        String[] p1 = timeRaw.split("_");
                        if(p1.length!=2) { throw new IllegalArgumentException(); }
                        day = Integer.parseInt(p1[0]);
                        String[] p2 = p1[1].split("-");
                        if(p2.length!=2) { throw new IllegalArgumentException(); }
                        start = Integer.parseInt(p2[0]);
                        end = Integer.parseInt(p2[1]);
                        if(day < 1 || day > 7 || start < 1 || start > 14 || end < 1 || end > 14 || start > end){
                            System.out.println("Illegal course time");
                            continue;
                        }
                    } catch (Exception ex) {
                        System.out.println("Illegal course time");
                        continue;
                    }
                    //时间冲突
                    boolean conflict = courses.values().stream()
                            .filter(c -> curUser.getId().equals(c.getTeacherId()))
                            .anyMatch(c -> c.getDay() == day && !(end <= c.getStart() || start >= c.getEnd()));
                    if(conflict){
                        System.out.println("Course time conflicts");
                        continue;
                    }
                    //学分解析
                    double credit;
                    try {
                        credit = Double.parseDouble(parts[3]);
                        if(!(credit >= 0.0 && credit <= 12.0)){
                            System.out.println("Illegal course credit");
                            continue;
                        }
                    } catch (Exception ex) {
                        System.out.println("Illegal course credit");
                        continue;
                    }
                    //学时解析
                    int period;
                    try {
                        period = Integer.parseInt(parts[4]);
                        if(!(period > 0 && period <= 1280)){
                            System.out.println("Illegal course period");
                            continue;
                        }
                    } catch (Exception ex) {
                        System.out.println("Illegal course period");
                        continue;
                    }
                    //所有检查通过
                    int idNum = ++courseCounter;
                    String courseId = "C-" + idNum;
                    Course course = new Course(courseId, courseName, currentUser, day, start, end, credit, period);
                    courses.put(courseId, course);
                    System.out.println("Create course success (courseId: " + courseId + ")");
                    continue;
                }

                if("listCourse".equals(command)){
                    if(currentUser==null){
                        System.out.println("No one is online");
                        continue;
                    }
                    User cur=users.get(currentUser);
                    DecimalFormat df=new DecimalFormat("#.#");
                    if(argCount==0){
                        //无参数情况
                        if(cur==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        //教师查看自己课程
                        if(cur.getRole()==Role.TEACHER){
                            List<Course> my=new ArrayList<>();
                            for(Course c:courses.values()){
                                if(cur.getId().equals(c.getTeacherId())){
                                    my.add(c);
                                }
                            }
                            if(my.isEmpty()){
                                System.out.println("Course does not exist");
                                continue;
                            }
                            my.sort(Comparator.comparingInt((c->parseCourseNum(c.getId()))));
                            for(Course c:my){
                                System.out.printf("%s %s %d_%d-%d %s %d%n",c.getId(),c.getName(),c.getDay(),c.getStart(),c.getEnd(),df.format(c.getCredit()),c.getPeriod());
                            }
                            System.out.println("List course success");
                            continue;
                        }
                        else{
                            if(courses.isEmpty()){
                                System.out.println("Course does not exist");
                                continue;
                            }
                            List<Course> all =new ArrayList<>(courses.values());
                            all.sort((a,b)->{
                                String ta=Optional.ofNullable(users.get(a.getTeacherId())).map(User::getUsername).orElse("");
                                String tb = Optional.ofNullable(users.get(b.getTeacherId())).map(User::getUsername).orElse("");
                                int cmp = ta.compareTo(tb);
                                if (cmp != 0) return cmp;
                                return Integer.compare(parseCourseNum(a.getId()), parseCourseNum(b.getId()));
                            });
                            for(Course c:all){
                                String tName = Optional.ofNullable(users.get(c.getTeacherId())).map(User::getUsername).orElse("");
                                System.out.printf("%s %s %s %d_%d-%d %s %d%n",
                                        tName,
                                        c.getId(),
                                        c.getName(),
                                        c.getDay(),c.getStart(),c.getEnd(),
                                        df.format(c.getCredit()),
                                        c.getPeriod());
                            }
                            System.out.println("List course success");
                            continue;
                        }
                    }
                    //有参数
                    else{
                        if(cur ==null){
                            System.out.println("No one is online");
                            continue;
                        }
                        if(cur.getRole()!=Role.ADMIN){
                            System.out.println("Permission denied");
                            continue;
                        }
                        String target=parts[1];
                        if(!isValidIdGeneral(target)){
                            System.out.println("Illegal user id");
                            continue;
                        }
                        if(!users.containsKey(target)){
                            System.out.println("User does not exist");
                            continue;
                        }
                        User targetUser=users.get(target);
                        if(targetUser.getRole()!=Role.TEACHER){
                            System.out.println("User id does not belong to a Teacher");
                            continue;
                        }
                        List<Course> tCourses=new ArrayList<>();
                        for(Course c:courses.values()){
                            if(target.equals(c.getTeacherId())) tCourses.add(c);
                        }
                        if(tCourses.isEmpty()){
                            System.out.println("Course does not exist");
                            continue;
                        }
                        tCourses.sort(Comparator.comparingInt(c-> parseCourseNum(c.getId())));
                        for(Course c:tCourses){
                            System.out.printf("%s %s %s %d_%d-%d %s %d%n",
                                    targetUser.getUsername(),
                                    c.getId(),
                                    c.getName(),
                                    c.getDay(), c.getStart(), c.getEnd(),
                                    df.format(c.getCredit()),
                                    c.getPeriod());
                        }
                        System.out.println("List course success");
                        continue;
                    }
                }

                if("selectCourse".equals(command)){
                    if(currentUser==null){
                        System.out.println("No one is online");
                        continue;
                    }
                    User cur =users.get(currentUser);
                    if(cur==null||cur.getRole()!=Role.STUDENT){
                        System.out.println("Permission denied");
                        continue;
                    }
                    String courseId=parts[1];
                    if(courseId==null || !courseId.matches("^C-\\d+$")){
                        System.out.println("Illegal course id");
                        continue;
                    }
                    try{
                        int num=Integer.parseInt((courseId.substring(2)));
                        if(num<=0){
                            System.out.println("Illegal course id");
                            continue;
                        }
                    }catch (Exception e){
                        System.out.println("Illegal course id");
                        continue;
                    }

                    if(!courses.containsKey(courseId)){
                        System.out.println("Course does not exist");
                        continue;
                    }
                    Course target=courses.get(courseId);
                    Set<String> students=courseStudents.getOrDefault(courseId,Collections.emptySet());
                    if(students.size()>=COURSE_CAPACITY){
                        System.out.println("Course is full");
                        continue;
                    }
                    boolean conflict=false;
                    Set<String> myCourses=studentCourses.getOrDefault(currentUser,Collections.emptySet());
                    for(String cid:myCourses){
                        Course c=courses.get(cid);
                        if(c==null) continue;
                        if(c.getDay()==target.getDay()&& !(target.getEnd()<c.getStart()||target.getStart()>c.getEnd())){
                            conflict=true;
                            break;
                        }
                    }
                    if(conflict){
                        System.out.println("Course time conflicts");
                        continue;
                    }
                    studentCourses.computeIfAbsent(currentUser,k->new HashSet<>()).add(courseId);
                    courseStudents.computeIfAbsent(courseId,k->new HashSet<>()).add(currentUser);
                    System.out.println("Select course success (courseId: "+courseId+")");
                    continue;
                }

                if("cancelCourse".equals(command)){
                    if(currentUser==null){
                        System.out.println("No one is online");
                        continue;
                    }
                    User cur=users.get(currentUser);
                    if(cur==null){
                        System.out.println("No one is online");
                        continue;
                    }
                    String courseId=parts[1];
                    if(courseId==null||!courseId.matches("^C-\\d+$")){
                        System.out.println("Illegal course id");
                        continue;
                    }
                    try{
                        int num=Integer.parseInt((courseId.substring(2)));
                        if(num<=0){
                            System.out.println("Illegal course id");
                            continue;
                        }
                    }catch (Exception e){
                        System.out.println("Illegal course id");
                        continue;
                    }
                    if(cur.getRole()==Role.STUDENT){
                        Set<String> myCourses=studentCourses.getOrDefault(courseId,Collections.emptySet());
                        if(!courses.containsKey(courseId)||!myCourses.contains(courseId)){
                            System.out.println("Course does not exist");
                            continue;
                        }
                        Set<String> updated =new HashSet<>(myCourses);
                        updated.remove(courseId);
                        if(updated.isEmpty()) studentCourses.remove(currentUser);
                        else studentCourses.put(currentUser,updated);
                        Set<String> studs=courseStudents.getOrDefault(courseId,Collections.emptySet());
                        if(!studs.isEmpty()){
                            Set<String> s2=new HashSet<>(studs);
                            s2.remove(currentUser);
                            if(s2.isEmpty()){
                                courseStudents.remove(courseId);
                            }
                            else{
                                courseStudents.put(currentUser,s2);
                            }
                        }
                        System.out.println("Cancel course success (courseId: "+courseId+")");
                        continue;
                    }
                    if(cur.getRole()!=Role.TEACHER && cur.getRole()!=Role.ADMIN){
                        System.out.println("Permission denied");
                        continue;
                    }
                    if(!courses.containsKey(courseId)){
                        System.out.println("Course does not exist");
                        continue;
                    }
                    Course target=courses.get(courseId);
                    if(cur.getRole()==Role.TEACHER&&!currentUser.equals(target.getTeacherId())){
                        System.out.println("Course does not exist");
                        continue;
                    }
                    Set<String> enrolled =courseStudents.getOrDefault(courseId,Collections.emptySet());
                    for(String sid:new HashSet<>(enrolled)){
                        Set<String>sc=studentCourses.getOrDefault(courseId,Collections.emptySet());
                        if(!sc.isEmpty()){
                            Set<String> newSc=new HashSet<>(sc);
                            newSc.remove(courseId);
                            if(newSc.isEmpty()){
                                studentCourses.remove(sid);
                            }
                            else{
                                studentCourses.put(sid,newSc);
                            }
                        }
                    }
                    courseStudents.remove(courseId);
                    courses.remove(courseId);
                    System.out.println("Cancel course success (courseId: "+courseId+")");
                    continue;
                }

                // 处理其他合法命令
                System.out.println("命令：" + command);
                if (argCount > 0) {
                    System.out.println("参数：");
                    for (int i = 1; i < parts.length; i++) {
                        System.out.println("参数" + i + ": " + parts[i]);
                    }
                } else {
                    System.out.println("无参数");
                }
            } else {
                System.out.println("未输入命令");
            }
        }
    }
    private static Role parseRole(String s){
        if(s==null) return null;
        String t=s.trim().toLowerCase();
        if(t.equals("administrator")){
            return Role.ADMIN;
        }
        if(t.equals("teacher")){
            return Role.TEACHER;
        }
        if(t.equals("student")){
            return Role.STUDENT;
        }
        try {
            return Role.valueOf(s.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
    private static boolean isValidName(String name){
        return name !=null && name.matches("^[A-Za-z][A-Za-z_]{3,15}$");
    }
    private static boolean isValidPassword(String pwd){
        if(pwd==null) return false;
        return pwd.matches("^(?=.{6,16}$)(?=.*[A-Za-z])(?=.*\\d)(?=.*[@_%$])[A-Za-z\\d@_%$]+$");
    }
    private static boolean isValidIdForRole(String id,Role role){
        if(id==null) return false;
        switch(role){
            case ADMIN:
                return id.matches("^AD(?!000)\\d{3}$");
            case TEACHER:
                return id.matches("^(?!0{5})\\d{5}$");
            case STUDENT:
                String undergrad = "^(19|20|21|22|23|24)(0[1-9]|[1-3][0-9]|4[0-3])[1-6](?!000)\\d{3}$";
                String master = "^(SY|ZY)\\d{7}$";
                String phd = "^BY\\d{7}$";
                return id.matches(undergrad) || id.matches(master) || id.matches(phd);
            default:
                return false;
        }
    }
    //通用学工号格式校验（用于login，不依赖role参数）
    private static boolean isValidIdGeneral(String id){
        if(id==null) return false;
        if (id.matches("^AD(?!000)\\d{3}$")) return true;
        if (id.matches("^(?!0{5})\\d{5}$")) return true;
        String undergrad = "^(19|20|21|22|23|24)(0[1-9]|[1-3][0-9]|4[0-3])[1-6](?!000)\\d{3}$";
        String master = "^(SY|ZY)\\d{7}$";
        String phd = "^BY\\d{7}$";
        return id.matches(undergrad) || id.matches(master) || id.matches(phd);
    }

    private  static void printUserInfo(User u){
        if(u==null) return ;
        System.out.println("User id: "+u.getId());
        System.out.println("Name: "+u.getUsername());
        System.out.println("Type: "+roleToString(u.getRole()));
    }

    private static String roleToString(Role role){
        if(role==null) return "";
        switch(role){
            case ADMIN: return "Administrator";
            case TEACHER:return "Teacher";
            case STUDENT:return "Student";
            default:return role.name();
        }
    }

    //把课程学分格式化为“最多保留一位小数”的字符串，用于输出
    private static String formatCredit(double credit){
        DecimalFormat df =new DecimalFormat("#.#");
        return df.format(credit);
    }

    //从课程 ID 中提取用于排序的整数编号（将课程编号 "C-1", "C-12" 等按数值大小排序），并在无法解析时返回一个很大的值以便把异常/无编号的课程排在后面。
    private static int parseCourseNum(String courseId){
        if(courseId==null){
            return Integer.MAX_VALUE;
        }
        try {
            if(courseId.startsWith("C-")){
                return Integer.parseInt(courseId.substring(2));
            }
            else{
                return Integer.parseInt(courseId.replaceAll("\\D",""));
            }
        }catch (Exception e){
            return Integer.MAX_VALUE;
        }
    }
}