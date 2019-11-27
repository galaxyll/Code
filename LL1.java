
import java.util.*;

/**
 * @author galaxy
 * @date 19-11-20 - 下午9:15
 */
public class LL1 {

    private final static int MAX_INDEX = 999999999;
    private final static String BLANK  = "ε";
    private final static String END = "#";
    private static int needNumber = 0;
    private static int IS_END = 0;

    private static ArrayList<String> input = new ArrayList<>();
    private static Node head ;
    private static ArrayList<String> starts = new ArrayList<>();
    private static ArrayList<String> childs = new ArrayList<>();
    private static Set<String> symbols = new HashSet<>();
    private static String aim;
    private static Map<String,HashSet<String>> firsts = new HashMap<>();
    private static Map<String,HashSet<String>> follows = new HashMap<>();
    private static Map<String,HashSet<String>> needButNon = new HashMap<>();
    private static Map<String,Map<String,HashSet<String>>> table = new HashMap<>();

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        scanner(in);
        analyze();
        delRecursive(head.pre);
        printNode(head);
        getFirst(head.pre);
        getFollow(head);
        dealNeedButNon();
        delFollowBlank();
        isLL1();
        generateSymbol(head);
        generateTable(head);
        printFF();
        printAnalyzeProcessTable(in);

    }


    private static void isLL1()
    {
        for (String start:
                starts) {
            if (firsts.get(start).contains(BLANK)){
                for (String first:
                        firsts.get(start)) {
                    if (follows.get(start).contains(first)){
                        System.out.println("该文法不是LL(1)文法");
                        return;
                    }
                }
            }
        }
        System.out.println("该文法是LL(1)文法");
    }
    private static void printAnalyzeProcessTable(Scanner in)
    {
        int index = 0;
        String inputString = in.next();
        Stack<String> stack = new Stack<>();
        Stack<String> utilStack = new Stack<>();
        stack.push("#");
        stack.push(head.start);
        System.out.println("符号栈\t输入串串\t所用产生式神");
        while (!END.equals(stack.peek())){
            System.out.print(stack.toString());
            System.out.print("\t"+inputString.substring(index));
            Set<String> productions = table.get(stack.peek()).get(String.valueOf(inputString.charAt(index)));
            if (!productions.isEmpty()){
                for (String production:
                     productions) {
                    System.out.println("\t"+stack.peek()+"->"+production);
                    String tmp;
                    stack.pop();
                    for (int i = 0; i < production.length(); i++) {
                        if (i<production.length()-1&&production.charAt(i+1)=='\''){
                            tmp = production.charAt(i)+"'";
                            i++;
                        }else {
                            tmp = String.valueOf(production.charAt(i));
                        }
                        if (!tmp.equals(BLANK)){

                            if (tmp.equals(String.valueOf(inputString.charAt(index)))){
                                index++;
                            }else {
                                utilStack.push(tmp);
                            }
                        }
                    }
                    while (!utilStack.empty()){
                        stack.push(utilStack.pop());
                    }
                }
            }else {
                System.out.println("匹配出错,忽略当前元素向后匹配：位置"+index);
                index++;
            }

        }
        System.out.print(stack.peek()+"\t"+inputString.substring(index+1));
    }
    private static void printAnalyzePredictionTable()
    {
        for (String symbol:
             symbols) {
            System.out.print("\t"+symbol);
        }
        System.out.println();
        for (String start:
             starts) {
            System.out.print(start);
            for (String symbol:
                 symbols) {
                System.out.print("\t"+table.get(start).get(symbol));
            }
            System.out.println();
        }
    }
    private static void generateSymbol(Node first)
    {
        int flag = 0;
        while (true){
            if (flag==1&&first==head){
                break;
            }
            for (String production:
                 first.productions) {
                for (int i = 0; i < production.length(); i++) {
                    if (!starts.contains(String.valueOf(production.charAt(i)))){
                        symbols.add(String.valueOf(production.charAt(i)));
                    }
                }
            }
            first = first.next;
            flag = 1;
        }
        symbols.add("#");
        symbols.remove("'");
        symbols.remove(BLANK);
    }
    private static void generateTable(Node first)
    {
        for (String start:
             starts) {
            table.put(start,new HashMap<>());
            for (String symbol:
                    symbols) {
                table.get(start).put(symbol,new HashSet<>());
            }
        }
        String ch;
        int flag = 0;
        while (true){
            if (flag==1&&first==head){
                break;
            }
            for (String symbol:
                 firsts.get(first.start)) {
                if (symbol.equals(BLANK)){
                    continue;
                }
                for (String production:
                     first.productions) {
                    if (production.equals(BLANK)){
                        continue;
                    }
                    ch = String.valueOf(production.charAt(0));

                    if ((symbols.contains(ch))){
                        if (symbol.equals(ch)){
                            table.get(first.start).get(symbol).add(production);
                        }
                    }else if (firsts.get(ch).contains(symbol)){
                        table.get(first.start).get(symbol).add(production);
                    }
                }
                if (firsts.get(first.start).contains(BLANK)){
                    for (String follow:
                         follows.get(first.start)) {
                        table.get(first.start).get(follow).add(BLANK);
                    }
                }
            }
            first = first.next;
            flag = 1;
        }
    }
    private static void delFollowBlank()
    {
        for (String start:
            starts ) {
            for (String production:
                 follows.get(start)) {
                if (production.equals(BLANK)){
                    follows.get(start).remove(BLANK);
                }
            }
        }
    }
    private static void getFollow(Node first)
    {
        int flag;
        for (String start:
             starts) {
            follows.put(start,new HashSet<>());
            needButNon.put(start,new HashSet<>());
        }
        for (String start:
             starts) {
            if (start.equals(head.start)){
                follows.get(start).add("#");
            }
            flag = 0;
            while (true){
                if (flag==1&&first==head){
                    break;
                }

                //if (!first.start.equals(start)){
                    for (int i = 0; i < first.productions.size(); i++) {
                        if (!start.contains("'")){
                            int startIndex = first.productions.get(i).indexOf(start);
                            if ((first.productions.get(i).contains(start)&&startIndex==(first.productions.get(i).length()-1))||(first.productions.get(i).contains(start)&&first.productions.get(i).charAt(startIndex+1)!='\'')){
                                if (startIndex==first.productions.get(i).length()-1){
                                    dealFollowNonTerminal(first.start,start,first.productions.get(i),1);
                                }else {
                                    dealFollowNonTerminal(first.start,start,first.productions.get(i),0);
                                }
                                                  }
                        } else if (first.productions.get(i).contains(start)){
                            int startIndex = first.productions.get(i).indexOf(start);
                            if (startIndex+1==first.productions.get(i).length()-1){
                                dealFollowNonTerminal(first.start,start,first.productions.get(i),1);
                            }else {
                                dealFollowNonTerminal(first.start,start,first.productions.get(i),0);
                            }

                        }
                    }
                //}
                first = first.next;
                if (flag == 0){
                    flag=1;
                }
            }
        }
    }
    private static void dealNeedButNon()
    {
        for (String start:
             starts) {
            if (!needButNon.get(start).isEmpty()){
                for (String sstart:
                     needButNon.get(start)) {
                    if (needButNon.get(sstart).isEmpty()){
                        follows.get(start).addAll(follows.get(sstart));
                        needButNon.get(start).remove(sstart);
                    }
                }
            }
        }
        for (String start:
             starts) {
            if (!needButNon.get(start).isEmpty()){
                dealNeedButNon();
            }
        }


    }
    private static void dealNeedButNon(String start,String nodeStart)
    {
        if (!follows.get(nodeStart).isEmpty()&&needButNon.get(nodeStart).isEmpty()){
            follows.get(start).addAll(follows.get(nodeStart));
        }else {
            needButNon.get(start).add(nodeStart);
        }
    }
    private static void dealFollowNonTerminal(String nodeStart,String start,String production,int isEnd)
    {
        int step;
        if (!start.contains("'")){
            step = 1;
        }else {
            step = 2;
        }
        if (start.equals(nodeStart)&&isEnd==1){
            return;
        }
        if (production.indexOf(start)==production.length()-step){
            dealNeedButNon(start,nodeStart);
        }else {
            String tmp;
            for (int i = production.indexOf(start)+step; i < production.length(); i++) {
                if (i+1<production.length()&&production.charAt(i+1)=='\''){
                    tmp = production.charAt(i)+"'";
                    i++;
                }else {
                    tmp = String.valueOf(production.charAt(i));
                }
                if (!starts.contains(tmp)){
                    follows.get(start).add(tmp);
                    break;
                }else if(!firsts.get(tmp).contains(BLANK)){
                    follows.get(start).addAll(firsts.get(tmp));
                    break;
                }else {
                    follows.get(start).addAll(firsts.get(tmp));
                    follows.get(start).remove(BLANK);
                    if (i==production.length()-1){
                        dealNeedButNon(start,nodeStart);
                    }
                }
            }
        }
    }
    private static void getFirst(Node last)
    {
        int flag = 0;
        String tmp;
        while (true){
            if (flag==1&&last==head.pre){
                break;
            }
            HashSet<String> set = new HashSet<>();
            firsts.put(last.start,set);
            for (String production:
                 last.productions) {
                tmp = String.valueOf(production.charAt(0));
                if (!starts.contains(tmp)){
                    firsts.get(last.start).add(tmp);
                }else {
                    dealFirstNonTerminal(last.start,production);
                }
            }

            last = last.pre;
            if (flag == 0){
                flag=1;
            }
        }
    }
    private static void dealFirstNonTerminal(String start,String production)
    {
        String tmp;
        int flag;
        for (int i = 0; i < production.length(); i++) {
            if (i<production.length()-1&&production.charAt(i+1)=='\''){
                tmp = production.charAt(i) +"'";
                i++;
            }else {
                tmp = String.valueOf(production.charAt(i));
            }
            if (i==production.length()-1){
                IS_END = 1;
            }
            flag = dealFirstNonTerUnit(start,tmp);
            if (flag==0){
                break;
            }

        }
    }
    private static int dealFirstNonTerUnit(String start,String tmp)
    {
        if (starts.contains(tmp)){
            if (firsts.get(tmp)==null){
                needNumber++;
                return 0;
            }else {
                firsts.get(start).addAll(firsts.get(tmp));
                if (firsts.get(tmp).contains(BLANK)){
                    if (IS_END!=1){
                        firsts.get(start).remove(BLANK);
                    }else {
                        IS_END = 0;
                    }
                    return 1;
                }else {
                    return 0;
                }
            }
        }else {
            firsts.get(start).add(tmp);
            return 0;
        }
    }
    private static void dealTerminal(Set<String> set,String production)
    {
        String tmp;
        for (int i = 0; i < production.length(); i++) {
            tmp = String.valueOf(production.charAt(i));
            if (!starts.contains(tmp)){
                set.add(tmp);
            }else {
                break;
            }
        }
    }
    private static void printNode(Node node)
    {
        int flag = 0;
        while (true){
            if (flag==1&&node==head){
                break;
            }
            System.out.printf("%s->",node.start);
            for (int i = 0; i < node.productions.size(); i++) {
                System.out.printf("%s",node.productions.get(i));
                if (i!=node.productions.size()-1){
                    System.out.print("|");
                }
            }
            System.out.print("\n");
            node = node.next;
            if (flag==0){
                flag = 1;
            }
        }
    }
    private static void printFF()
    {
        for (String start:
             starts) {
            System.out.print("FIRST(" + start + ")=");
            System.out.println(firsts.get(start));
            System.out.print("FOLLOW(" + start + ")=");
            System.out.println(follows.get(start));
        }
    }
    private static void delNode(Node node)
    {
        node.pre.next = node.next;
        node.next.pre = node.pre;
    }
    private static void delRecursive(Node last)
    {
        int flag = 0;
        while (true){
            if (flag==1&&last==head.pre){
                break;
            }
            for (int i = 0; i < last.productions.size(); i++) {
                if (starts.contains(String.valueOf(last.productions.get(i).charAt(0)))&&String.valueOf(last.productions.get(i).charAt(0)).equals(last.start)){
                    System.out.println("检测到直接左递归");
                    getChild(last,i);
                    direct(last,i);
                    break;
                }else if (starts.contains(String.valueOf(last.productions.get(i).charAt(0)))&&starts.indexOf(String.valueOf(last.productions.get(i).charAt(0)))<last.number){
                    //&&!String.valueOf(last.productions.get(i).charAt(0)).equals(last.next.start)
                    System.out.println("检测到间接左递归");
                    aim = String.valueOf(last.productions.get(i).charAt(0));
                    indirect(last);
                    return;
                }
            }
            last = last.pre;
            flag=1;
        }
    }
    private static void indirect(Node node)
    {
        while (!node.start.equals(aim)){
            getChild(node.pre, MAX_INDEX);
            node.pre.productions.clear();
            for (String child:
                 childs) {
                childReplace(node,child);
            }
            starts.remove(node.start);
            delNode(node);
            node = node.pre;
        }
        int index = -2;
        for (String str:
             node.productions) {
            if ((findIndex(str,node.start.charAt(0))>=0)){
                index = findIndex(str,node.start.charAt(0));
                break;
            }
        }

        getChild(node,index);
        direct(node,index);

    }
    private static int findIndex(String str,char ch)
    {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i)==ch){
                return i;
            }
        }
        return -1;
    }
    private static void childReplace(Node node,String child)
    {
        ArrayList<String> strings = null;
        StringBuilder builder;
        int index = -1;
        for (int i = 0; i < child.length(); i++) {
            if (String.valueOf(child.charAt(i)).equals(node.start)){
                index = i;
            }
        }
        if (index!=-1){
            strings = new ArrayList<>();
            for (int i = 0; i < node.productions.size(); i++) {
                builder = new StringBuilder(child);
                builder.replace(index,index+1,node.productions.get(i));
                strings.add(builder.toString());
            }
            node.pre.productions.addAll(strings);
        }else {
            node.pre.productions.add(child);
        }

    }
    private static void direct(Node node,int index)
    {
        Node tmp = new Node();
        node.next.pre = tmp;
        tmp.next = node.next;
        node.next = tmp;
        tmp.pre = node;
        tmp.start = node.start+"'";
        starts.add(tmp.start);
        tmp.productions.add(node.productions.get(index).substring(1)+tmp.start);
        tmp.productions.add("ε");
        node.productions.clear();
        for (String str:
                childs) {
            node.productions.add(str+node.start+"'");
        }
    }
    private static void getChild(Node node,int index)
    {
        childs.clear();
        for (int i = 0; i < node.productions.size(); i++) {
            if (i!=index){
                childs.add(node.productions.get(i));
            }
        }
    }
    private static void analyze()
    {
        int number = 0;
        Node node = null;
        Node prev = null;
        for (String production:
             input) {

            node = new Node();
            if (prev!=null){
                prev.next=node;
            }else {
                head = node;
            }
            node.pre = prev;
            String[] strs = production.split("\\|");
            for (int i=0;i<strs[0].length();i++){
                if (strs[0].charAt(i)=='-'&&strs[0].charAt(i+1)=='>'){
                    node.start = strs[0].substring(0,i);
                    node.number = number++;
                    starts.add(node.start);
                    node.productions.add(strs[0].substring(i+2));
                    node.productions.addAll(Arrays.asList(strs).subList(1, strs.length));
                    break;
                }
            }
            prev = node;
        }
        head.pre = node;
        node.next = head;
    }
    private static void scanner(Scanner in)
    {

        String production;
        while(true){
            production = in.next();
            if ("end".equals(production)){
                break;
            }
            input.add(production);
        }
    }
    static class Node{
        String start = null;
        ArrayList<String> productions = new ArrayList<>();
        Node pre;
        Node next;
        int number;
    }
}
