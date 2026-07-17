package za.ac.cput.carrental.service;

public interface Iservice <T ,ID>{
    T create (T t);
    T read (ID id);
    T update (T t);
   boolean delete (ID id);
}
