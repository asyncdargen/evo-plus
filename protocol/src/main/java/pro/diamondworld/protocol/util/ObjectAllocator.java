package pro.diamondworld.protocol.util;

import lombok.SneakyThrows;
import lombok.val;
import sun.misc.Unsafe;

public interface ObjectAllocator {

    static ObjectAllocator UNSAFE = new UnsafeAllocator();

    <T> T allocate(Class<T> objectClass);

    class UnsafeAllocator implements ObjectAllocator{

        private final Unsafe unsafe;

        @SneakyThrows
        public UnsafeAllocator() {
            val unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            unsafe = (Unsafe) unsafeField.get(null);
        }

        @Override
        @SneakyThrows
        @SuppressWarnings("unchecked")
        public <T> T allocate(Class<T> objectClass) {
            return (T) unsafe.allocateInstance(objectClass);
        }

    }

}
