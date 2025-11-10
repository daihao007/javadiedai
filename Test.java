import java.util.*;

public class Test {
    public static void main(String[] args) {
        // 定义已知命令及其参数数量范围
        Map<String, int[]> commandArgsRange = new HashMap<>();
        commandArgsRange.put("start", new int[]{0, 2}); // 最少0个参数，最多2个参数
        commandArgsRange.put("restart", new int[]{1, 1}); // 恰好1个参数
        commandArgsRange.put("quit", new int[]{0, 0}); // 不接受参数

        List<String> loggedInUsers = new ArrayList<>();
        loggedInUsers.add("22371001");
        loggedInUsers.add("22371002");

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入命令和参数：");

        while (true) {
            // 等待用户输入
            String input = scanner.nextLine().trim();
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
                    System.out.println("Invalid number of arguments for command '" + command + "'");
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
}