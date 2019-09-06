package utils;

import optics.objects.Interactive;

import java.util.ArrayList;
import java.util.Collections;

public class OpticsList<T extends Interactive> extends ArrayList<T> {

  public <T> OpticsList() {
    super();
  }

  public void addAll(T... args){
    Collections.addAll(this, args);
  }

  public OpticsList<T> getAllExcept(T exclude){
    OpticsList<T> result = new OpticsList<>();
    for(T elem: this){
      if(!elem.equals(exclude))result.add(elem);
    }
    return result;
  }

  public OpticsList<T> deepClone(){
    OpticsList<T> result = new OpticsList<>();
    for(T elem: this){
      result.add((T) elem.cloneObject());
    }
    return result;
  }
}
