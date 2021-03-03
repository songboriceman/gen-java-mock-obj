

import org.reflections.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;


public class MockUtil {

    private static final Boolean[] bools                 = new Boolean[] { true, false };

    private static final char[]    words                 = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    private static final Random    r                     = new Random();

    private static final int       MAX_COLLECTION_LENGTH = 3;

    private static final int       MAX_STRING_LENGTH     = 15;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> T mock(Class<T> clazz) {
        if (clazz == Character.class || clazz == Character.TYPE) {
            return (T) (Character) words[r.nextInt(words.length)];
        } else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
            return (T) (Boolean) bools[r.nextInt(bools.length)];
        } else if (clazz == Long.class || clazz == Long.TYPE) {
            return (T) (Long) r.nextLong();
        } else if (clazz == Integer.class || clazz == Integer.TYPE) {
            return (T) (Integer) r.nextInt();
        } else if (clazz == Short.class || clazz == Short.TYPE) {
            return (T) (Short) new Integer(r.nextInt(127)).shortValue();
        } else if (clazz == Float.class || clazz == Float.TYPE) {
            return (T) (Float) r.nextFloat();
        } else if (clazz == Double.class || clazz == Double.TYPE) {
            return (T) (Double) r.nextDouble();
        } else if (clazz == String.class) {
            return (T) randString(r.nextInt(MAX_STRING_LENGTH));
        }

        try {
            T instance = clazz.newInstance();

            for (Field f : ReflectionUtils.getAllFields(clazz)) {// getAllFields 返回本类和超类全部字段,getFields仅返回当前类成员
                f.setAccessible(true);

                if (f.getType() == Character.TYPE) {
                    f.setChar(instance, words[r.nextInt(words.length)]);
                } else if (f.getType() == Character.class) {
                    f.set(instance, words[r.nextInt(words.length)]);
                } else if (f.getType() == Boolean.TYPE) {
                    f.setBoolean(instance, bools[r.nextInt(bools.length)]);
                } else if (f.getType() == Boolean.class) {
                    f.set(instance, bools[r.nextInt(bools.length)]);
                } else if (f.getType() == Long.TYPE) {
                    f.setLong(instance, r.nextLong());
                } else if (f.getType() == Long.class) {
                    f.set(instance, r.nextLong());
                } else if (f.getType() == Integer.TYPE) {
                    f.setInt(instance, r.nextInt());
                } else if (f.getType() == Integer.class) {
                    f.set(instance, r.nextInt());
                } else if (f.getType() == Short.TYPE) {
                    f.setShort(instance, new Integer(r.nextInt(127)).shortValue());
                } else if (f.getType() == Short.class) {
                    f.set(instance, new Integer(r.nextInt(127)).shortValue());
                } else if (f.getType() == Float.TYPE) {
                    f.setFloat(instance, r.nextFloat());
                } else if (f.getType() == Float.class) {
                    f.set(instance, r.nextFloat());
                } else if (f.getType() == Double.TYPE) {
                    f.setDouble(instance, r.nextDouble());
                } else if (f.getType() == Double.class) {
                    f.set(instance, r.nextDouble());
                } else if (f.getType() == String.class) {
                    f.set(instance, randString(r.nextInt(MAX_STRING_LENGTH)));
                } else if (f.getType() == List.class) {
                    f.set(instance, generateList((ParameterizedType)f.getGenericType()));
                }else if (f.getType() == Set.class) {
                    f.set(instance, generateSet((ParameterizedType)f.getGenericType()));
                }  else if (f.getType() == Map.class) {
                    ParameterizedType pt = (ParameterizedType) f.getGenericType();
                    f.set(instance, generateMap(pt));
                } else if (f.getType() == Date.class) {
                    f.set(instance, new Date());
                }  else if (f.getType().isArray()) {// 处理数组
                    f.set(instance, generateArray( f.getType()));
                }else {
                    f.set(instance, mock(f.getType()));
                }
            }

            return instance;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }


    private static Map<Object, Object> generateMap( ParameterizedType pt) {
        Map<Object, Object> map = new HashMap<>();
        int size = r.nextInt(MAX_COLLECTION_LENGTH)+1;
        for (int i = 0; i < size; i++) {
            Object key = null,value = null;

            //生成key,假设key不可能为List或者Map
            if(pt.getActualTypeArguments()[0] instanceof Class){
                key = mock((Class) pt.getActualTypeArguments()[0]);
            }else{
                throw new IllegalArgumentException("not supported key type:"+pt.getActualTypeArguments()[0].getTypeName());
            }

            // 生成value
            if(pt.getActualTypeArguments()[1] instanceof Class){
                value =  mock((Class) pt.getActualTypeArguments()[1]);
            }else if(pt.getActualTypeArguments()[1] instanceof ParameterizedType){
                ParameterizedType ptype = (ParameterizedType) pt.getActualTypeArguments()[1];
                if(ptype.getRawType() == List.class){
                    List<Object> list = generateList(ptype);
                    value = list;
                }else if(ptype.getRawType() == Map.class){
                    value = generateMap((ParameterizedType) ptype.getRawType());
                }else if(ptype.getRawType() == Set.class){
                    value = generateSet(ptype);
                }
            }else{
                throw new IllegalArgumentException("not supported value type:"+pt.getActualTypeArguments()[1].getTypeName());
            }

            // 给map赋值
            map.put(key, value);
        }
        return map;

    }

    private static Object[] generateArray(Class f) {
        int sizeInner = r.nextInt(MAX_COLLECTION_LENGTH)+1;

        Object[] objects = (Object[]) Array.newInstance(f.getComponentType(),sizeInner);
        for (int i=0;i<objects.length;++i) {
            objects[i] = mock(f.getComponentType());
        }
        return objects;
    }

    private static List<Object> generateList(ParameterizedType f) {
        int sizeInner = r.nextInt(MAX_COLLECTION_LENGTH)+1;
        List<Object> list = new ArrayList<Object>(sizeInner);
        ParameterizedType ptInner = f;
        for (int ii = 0; ii < sizeInner; ii++) {
            if (ptInner.getActualTypeArguments()[0] instanceof Class) {
                list.add(mock((Class) ptInner.getActualTypeArguments()[0]));
            } else if (ptInner.getActualTypeArguments()[0] instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) ptInner.getActualTypeArguments()[0];
                if(ptype.getRawType() == List.class){
                    List<Object> obj = generateList(ptype);
                    list.add(obj);
                }else if(ptype.getRawType() == Map.class){
                    list.add(generateMap(ptype));
                }else if(ptype.getRawType() == Set.class){
                    list.add(generateSet(ptype));
                }else {
                    throw new IllegalArgumentException("not supported type:"+ptype.getTypeName());
                }
            }
        }
        return list;
    }

    private static Set<Object> generateSet(ParameterizedType f) {
        int sizeInner = r.nextInt(MAX_COLLECTION_LENGTH)+1;
        Set<Object> set = new HashSet<>(sizeInner);
        ParameterizedType ptInner = f;
        for (int ii = 0; ii < sizeInner; ii++) {
            if (ptInner.getActualTypeArguments()[0] instanceof Class) {
                set.add(mock((Class) ptInner.getActualTypeArguments()[0]));
            } else if (ptInner.getActualTypeArguments()[0] instanceof ParameterizedType) {
                ParameterizedType ptype = (ParameterizedType) ptInner.getActualTypeArguments()[0];
                if(ptype.getRawType() == List.class){
                    set.add( generateList(ptype));
                }else if(ptype.getRawType() == Map.class){
                    set.add(generateMap(ptype));
                }else if(ptype.getRawType() == Set.class){
                    set.add(generateSet(ptype));
                }else {
                    throw new IllegalArgumentException("not supported type:"+ptype.getTypeName());
                }
            }
        }
        return set;
    }


    private static String randString(int count) {
        if (count == 0) {
            count = 1;
        }

        int length = words.length;
        char[] text = new char[count];
        for (int i = 0; i < count; i++) {
            text[i] = words[r.nextInt(length)];
        }

        return new String(text);
    }

}