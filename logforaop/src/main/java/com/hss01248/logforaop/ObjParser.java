package com.hss01248.logforaop;



import android.content.Intent;
import android.os.Message;
import android.util.Pair;



import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Kale
 * @date 2016/3/23
 */
public class ObjParser {

    public static final String SPACE = "  ";
    public static final String NEWLINE_AND_SPACE = "\n  ";
    // 基本数据类型
    private final static String[] TYPES = {"int", "java.lang.String", "boolean", "char",
            "float", "double", "long", "short", "byte",
            "java.lang.Integer","java.lang.Boolean",
            "java.lang.Float","java.lang.Double",
            "java.lang.Char","java.lang.Short",
            "java.lang.Byte"};

    public static String parseObj(Object object) {
        return parseObj(object,false,true);
    }

    public static String parseObj(Object object,boolean printMethod,boolean ignoreToStringImpl) {
        if (object == null) {
            return "null";
        }

        final String simpleName = object.getClass().getSimpleName();
        if (object.getClass().isArray()) {
            StringBuilder msg = new StringBuilder("Temporarily not support more than two dimensional Array!");
            int dim = ArrayParser.getArrayDimension(object);
            switch (dim) {
                case 1:
                    Pair pair = ArrayParser.arrayToString(object);
                    msg = new StringBuilder(simpleName.replace("[]", "[" + pair.first + "] {\n  "));
                    msg.append(pair.second).append(NEWLINE_AND_SPACE);
                    break;
                case 2:
                    Pair pair1 = ArrayParser.arrayToObject(object);
                    Pair pair2 = (Pair) pair1.first;
                    msg = new StringBuilder(simpleName.replace("[][]", "[" + pair2.first + "][" + pair2.second + "] {\n  "));
                    msg.append(pair1.second) .append(NEWLINE_AND_SPACE);
                    break;
                default:
                    break;
            }
            return msg + "}";
        } else if (object instanceof Collection) {
            Collection collection = (Collection) object;
            //String msg = "%s size = %d [\n";
            StringBuilder sb = new StringBuilder();
            sb.append(simpleName)
                    .append(" size = ")
                    .append(collection.size())
                    .append(" [\n");
            //msg = String.format(Locale.ENGLISH, msg, simpleName, collection.size());
            if (!collection.isEmpty()) {
                Iterator iterator = collection.iterator();
                int flag = 0;
                while (iterator.hasNext()) {
                    //String itemString = "[%d]:%s%s\n";
                    Object item = iterator.next();
                    sb.append("[")
                            .append(flag)
                            .append("]:")
                            .append(objectToString(item,printMethod,ignoreToStringImpl))
                            .append(flag++ < collection.size() - 1 ? ",\n  " : "  ");

                   /* msg += String.format(Locale.ENGLISH, itemString,
                            flag,
                            objectToString(item,printMethod,ignoreToStringImpl),
                            flag++ < collection.size() - 1 ? ",\n  " : "  ");*/
                }
            }
            return sb.append("]").toString() ;
        } else if (object instanceof Map) {
            StringBuilder sb = new StringBuilder();
            sb.append(simpleName)
                    .append( " {\n");
            //String msg = simpleName + " {\n";
            Map map = (Map) object;
            Set keys = map.keySet();
            for (Object key : keys) {
                String itemString = "[%s -> %s]\n\n";
                Object value = map.get(key);
                sb.append("[")
                        .append(objectToString(key,printMethod,ignoreToStringImpl))
                        .append(" -> ")
                        .append(objectToString(value,printMethod,ignoreToStringImpl));
                //msg += String.format(itemString, objectToString(key,printMethod,ignoreToStringImpl), objectToString(value,printMethod,ignoreToStringImpl));
            }
            return sb.append("}").toString();
        } else {
            return objectToString(object,printMethod,ignoreToStringImpl);
        }
    }


    protected static <T> String objectToString(T object) {
        return objectToString(object,false,true);
    }

    /**
     * 将对象转化为String
     */
    protected static  String objectToString(Object object,boolean printMethod,boolean ignoreToStringImpl) {
        if (object == null) {
            return "Object{object is null}";
        }
        /*if(object instanceof Reference){
            Reference reference = (Reference) object;
           Object object2 = reference.get();
            if (object2 == null) {
                return reference.toString()+"{real object is null}";
            }
            object = object2;
        }*/
        String toStr = object.toString();


        String type0 = object.getClass().getName();
        boolean isSimpleType = false;
        for (String type : TYPES) {
            if(type.equals(type0)){
                isSimpleType = true;
                break;
            }
        }
        if(isSimpleType){
            return object.toString();
        }

        //忽略类本身的tostring实现,强制打印所有属性和方法
        if(!toStr.startsWith(object.getClass().getName() + "@")){
            if(!ignoreToStringImpl){
                return toStr;
            }
        }

        StringBuilder builder = new StringBuilder(toStr + "{");
        builder.append(NEWLINE_AND_SPACE);
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            boolean flag = false;

            //不打印静态且final的属性:
            if(Modifier.isStatic(field.getModifiers())
                    && Modifier.isFinal(field.getModifiers())){
                continue;
            }
            for (String type : TYPES) {
                if (field.getType().getName().equalsIgnoreCase(type)) {
                    flag = true;
                    Object value = null;
                    try {
                        value = field.get(object);
                        //intent
                        /*if(object instanceof Intent && "mFlags".equals(field.getName())){
                            value = IntentParse.getFlags((Integer) value);
                        }*/
                    } catch (IllegalAccessException e) {
                        value = e;
                    } finally {
                        //规则1:只打印一层,内部不再解析,而是用toString
                        //规则2: null值不打印
                        if(value != null){
                            builder.append(field.getName())
                                    .append(" = ")
                                    .append(value.toString())
                                    .append(",\n")
                                    //.append(String.format("%s = %s,\n", field.getName(),  value.toString()))
                                    .append(NEWLINE_AND_SPACE);
                        }

                    }
                }
            }
            if (!flag) {
                try {
                    Object objectf = field.get(object);

                    //规则同上,但如果是一些特殊的类,可以再次打印
                    if(objectf != null){
                        String objStr =  formatInnerObj(objectf);
                        builder.append(field.getName())
                                .append(" = ")
                                .append(objStr)
                                .append(",\n")
                                //.append(String.format("%s = %s,\n", field.getName(), objStr))
                                .append(NEWLINE_AND_SPACE);//"Object"
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    builder.append(field.getName())
                            .append(" = ")
                            .append("object")
                            .append(",\n")
                            //.append(String.format("%s = %s,\n", field.getName(), "object"))
                            .append(NEWLINE_AND_SPACE);
                }
            }
        }

        if(printMethod){
            builder.append("\n\n methods:\n");
            Method[] methods =   object.getClass().getDeclaredMethods();
            for (Method method : methods){
                method.setAccessible(true);
                String name = method.getName();
                if(name.contains("$")){
                    continue;
                }

                Class[] clazz = method.getParameterTypes();
                StringBuilder stringBuilder = new StringBuilder();
                if(clazz != null && clazz.length >0){
                    for (int i =0; i< clazz.length; i++){
                        stringBuilder.append(clazz[i].getSimpleName());
                        if(i != clazz.length-1){
                            stringBuilder.append(", ");
                        }
                    }
                }
                String params = stringBuilder.toString();

                builder
                        .append("\nanotations:")
                        .append(Arrays.toString(method.getAnnotations()))
                        .append("\n")
                        .append(method.getReturnType())
                        .append(" ")
                        .append(method.getName())
                        .append("(")
                        .append(params)
                        .append(")")
                        .append("\n");
            }
        }
       /* int idx = builder.lastIndexOf(",");
        if(idx > 0){
            return builder.replace(idx, idx+2, "  }").toString();
        }*/
        return builder.toString();
    }

    private static String formatInnerObj(Object objectf) {
        if(objectf == null){
            return null;
        }
        if(objectf instanceof Intent){
            return objectToString(objectf,false,true);
        }
        if(objectf instanceof Message){
            return objectToString(objectf);
        }
        if(objectf.getClass().isArray()){
            try {
                return Arrays.toString((Object[]) objectf);
            }catch (Throwable e){
                e.printStackTrace();
                return objectf+"";
            }

        }
        return objectf.toString();
    }
}

