package io.jpress.utils;

import io.jpress.model.User;

import java.util.UUID;

import com.jfinal.kit.HashKit;

public class HashUtils extends HashKit{
	
	public static String salt(){
		int random = (int)(10+(Math.random() * 10 ));
		return UUID.randomUUID().toString().replace("-", "").substring(random);
	}
	
	
	public static String md5WithSalt(String text,String salt){
		return md5(md5(text)+salt).substring(0,20);
	}
	
	public static boolean verlify(User user,String password){
		return user.getPassword().equals(md5WithSalt(password,user.getSalt()));
	}
	
	public static String generateUcode(User user) {
		return md5(user.getSalt() +  user.getId());
	}

	
	public static void main(String[] args) {
//		System.out.println(md5WithSalt("123456","123"));
		//51e34a82801b3a98396e, d632686d14972f3
		System.out.println(md5WithSalt("xxx","d632686d14972f3"));
	}

}
