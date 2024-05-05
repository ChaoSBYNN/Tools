```java
        List<String> source = new ArrayList<>();
        source.add("A");
        source.add("B");
        source.add("C");
        source.add("D");
        ArrayList<String> target = new ArrayList<>();
        target.add("D");
        target.add("E");
        target.add("F");
        target.add("G");

        // 交集
        List<String> intersection = source.stream().filter(item -> target.contains(item)).collect(Collectors.toList());
        System.out.print("交集-->");
        intersection.parallelStream().forEach(System.out::print);
        System.out.println();

        // 差集 (list1 - list2)
        List<String> reduce1 = source.stream().filter(item -> !target.contains(item)).collect(Collectors.toList());
        System.out.print("差集(source - target)-->");
        reduce1.parallelStream().forEach(System.out::print);
        System.out.println();

        // 差集 (list2 - list1)
        List<String> reduce2 = target.stream().filter(item -> !source.contains(item)).collect(Collectors.toList());
        System.out.print("差集(target - source)-->");
        reduce2.parallelStream().forEach(System.out::print);
        System.out.println();

        // 并集
        List<String> listAll = source.parallelStream().collect(Collectors.toList());
        List<String> listAll2 = target.parallelStream().collect(Collectors.toList());
        listAll.addAll(listAll2);
        System.out.print("并集-->");
        listAll.parallelStream().forEachOrdered(System.out::print);
        System.out.println();

        // 去重并集
        List<String> listAllDistinct = listAll.stream().distinct().collect(Collectors.toList());
        System.out.print("并集去重-->");
        listAllDistinct.parallelStream().forEachOrdered(System.out::print);
```
