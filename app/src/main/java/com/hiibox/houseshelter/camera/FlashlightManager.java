package com.hiibox.houseshelter.camera;

import android.os.IBinder;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class FlashlightManager {

  private static final String TAG = FlashlightManager.class.getSimpleName();

  private static final Object iHardwareService;
  private static final Method setFlashEnabledMethod;
  static {
    iHardwareService = getHardwareService();
    setFlashEnabledMethod = getSetFlashEnabledMethod(iHardwareService);

  }

  private FlashlightManager() {
  }

  private static Object getHardwareService() {
    Class<?> serviceManagerClass = maybeForName("android.os.ServiceManager");
    if (serviceManagerClass == null) {
      return null;
    }

    Method getServiceMethod = maybeGetMethod(serviceManagerClass, "getService", String.class);
    if (getServiceMethod == null) {
      return null;
    }

    Object hardwareService = invoke(getServiceMethod, null, "hardware");
    if (hardwareService == null) {
      return null;
    }

    Class<?> iHardwareServiceStubClass = maybeForName("android.os.IHardwareService$Stub");
    if (iHardwareServiceStubClass == null) {
      return null;
    }

    Method asInterfaceMethod = maybeGetMethod(iHardwareServiceStubClass, "asInterface", IBinder.class);
    if (asInterfaceMethod == null) {
      return null;
    }

    return invoke(asInterfaceMethod, null, hardwareService);
  }

  private static Method getSetFlashEnabledMethod(Object iHardwareService) {
    if (iHardwareService == null) {
      return null;
    }
    Class<?> proxyClass = iHardwareService.getClass();
    return maybeGetMethod(proxyClass, "setFlashlightEnabled", boolean.class);
  }

  private static Class<?> maybeForName(String name) {
    try {
      return Class.forName(name);
    } catch (ClassNotFoundException cnfe) {
           
      return null;
    } catch (RuntimeException re) {

      return null;
    }
  }

  private static Method maybeGetMethod(Class<?> clazz, String name, Class<?>... argClasses) {
    try {
      return clazz.getMethod(name, argClasses);
    } catch (NoSuchMethodException nsme) {
           
      return null;
    } catch (RuntimeException re) {

      return null;
    }
  }

  private static Object invoke(Method method, Object instance, Object... args) {
    try {
      return method.invoke(instance, args);
    } catch (IllegalAccessException e) {

      return null;
    } catch (InvocationTargetException e) {

      return null;
    } catch (RuntimeException re) {

      return null;
    }
  }

  static void enableFlashlight() {
    setFlashlight(true);
  }

  static void disableFlashlight() {
    setFlashlight(false);
  }

  private static void setFlashlight(boolean active) {
    if (iHardwareService != null) {
      invoke(setFlashEnabledMethod, iHardwareService, active);
    }
  }

}
