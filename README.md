PHP template engine
---
xml config file
--
sample
```xml
<?xml version="1.0" encoding="UTF-8"?>
<templateconfig src="Resources" dest="View" namespace="App\View">
<template name="FirstPage" />
<template name="SecondPage" />
</templateconfig> 
```

src=source path, may be relative to xml config file
dest=destination base path for compiled output
```xml
<template name="FirstPage" />
```
expects a html file in subfolder Resources/Templates/FirstPage.html

template elements
--
1. html escaped output
  ```
  {$data}
  ```
  compiles as 
  ```php
  echo htmlentities($data)
  ```
2. raw output  
  ```
  {{$data}}
  ```
  compiles as 
  ```php
  echo $data
  ```
3. conditional

a)
  ```
  <p:if cond="$condition">
  <!-- 'then' case here  -->
  </p:if>
  ```
  compiles as 
  ```php
  if($condition) {
    // 'then' case here
  }
  ```
b)  
  ```
  <p:if cond="$condition">
  <p:then>
  <!-- 'then' case here  -->
  </p:then>
  <p:else>
  <!-- 'else' case here  -->
  </p:else>  
  </p:if>
  ```
   compiles as 
  ```php
  if($condition) {
    // 'then' case here
  } else {
    // 'else' case here
  }
  ```
