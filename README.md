# Kac-interpreter

This is dynamically typed language where every statement ends with `;`. Scope is defined by `{` and `}`. 

Logic operators:\
`and` - logical AND\
`or` - logical OR\
`==` - comparison between two values

Variable declaration looks like this:

**var** _variable_name_ = _variable_value_;

For example:
```
var a=10;
var b="Programming is fun";
var c=2.137;
```

For loop declaration:

**for**(_initializer_; _condition_; _state_modifier_){\
   //some code here\
}
  
Example:
```
for(var i=0;i<10;i=i+1){
  print i;
}
```

Analogically while loop declaration looks like this:

**while**(_condition_){\
  //some code here\
}
  
Example:
```
while(1){
  print "this is endless loop";
}

var i=0;
while(i<10){
  print i;
  i=i+1;
}
```

Functions:

**fun** _function_name_ (_variable1_, _variable2_,...){
  //some code here
}
  
Example:
```
fun fibbonaci(n){
	if(n==0)
		return 0;
	if(n==1 or n==2)
		return 1;
	var result=0;
	var a=1;
	var b=1;

	for(var i=2;i<n;i=i+1){
		result=a+b;
		a=b;
		b=result;
	}
	
	return result;
}

print fibbonaci(10);
```

Including other files:\
`#import` pastes source code from imported file.

`fib.ka` file:
```
fun fibbonaci(n){
	if(n==0)
		return 0;
	if(n==1 or n==2)
		return 1;
	var result=0;
	var a=1;
	var b=1;

	for(var i=2;i<n;i=i+1){
		result=a+b;
		a=b;
		b=result;
	}
	
	return result;
}
```
`script.ka` file:
```
#import fib.ka;

print fibbonaci(15);
```

References:
https://craftinginterpreters.com/contents.html
