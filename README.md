# 국코칭


## 0. Version and Execution Environment



### Version

```kotlin
compileSdkVersion 29
buildToolsVersion '30.0.2'

minSdkVersion 21
targetSdkVersion 29
```



### Execution Environment

* **Used AVD**: Pixel 3 API 29

* **Used Library (기본으로 설치된 것 제외)**

```kotlin
// Firebase를 연동하고 사용
implementation 'com.google.firebase:firebase-firestore:21.7.1'
implementation 'com.google.firebase:firebase-storage:19.1.1'
implementation 'com.google.firebase:firebase-auth:20.0.0'
implementation 'com.google.firebase:firebase-database:19.5.1'

// Jsoup을 이용한 Crawling
implementation 'org.jsoup:jsoup:1.11.3'

// 동기, 비동기 문제를 해결하기 위해 코루틴 사용
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'

// 게시글과 댓글 목록을 꾸미기 위해 CardView 사용
implementation 'androidx.cardview:cardview:1.0.0'

// 게시글과 댓글 목록을 보여주기 위해 RecyclerView 사용
implementation "androidx.recyclerview:recyclerview:1.1.0"
implementation "androidx.recyclerview:recyclerview-selection:1.1.0-rc03"

// 이미지 업로드를 위해 Multi Image Picker 사용
implementation 'gun0912.ted:tedbottompicker:2.0.1'
implementation 'gun0912.ted:tedpermission:2.2.3'

// Url 주소에 맞는 사진을 ImageView와 연동 후 사용
implementation 'com.github.bumptech.glide:glide:4.9.0'

// 코루틴을 쉽게 다룰 수 있는 Task.await를 사용
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.2.1'

```

***

## 1. App structure



**본 프로젝트의 중심화면은 4개의 Fragment로 구성**

* Home (홈)
* ShardBoard (지식 공유 게시판)
* MajorBoard (전공수업 지식 공유 게시판)
* Project (프로젝트원 모집 게시판)



**사용 위젯**

* 화면 전환을 위해 Bottom navigation view 사용
* 좌우 Scrolling이 가능한 ViewPager 사용



***

## 1-1 Intro

<img src="https://user-images.githubusercontent.com/28584275/99692150-5228cd00-2acd-11eb-8762-86453bed107d.jpg" width="300" float="left">



## 2. Login, Register  screen

**로그인**

* 이메일과 비밀번호를 입력하여 앱에 로그인

  * 등록된 사용자인 경우: MainActivity로 이동
* 등록되지 않은 사용자인 경우: "등록된 사용자가 아닙니다"를 Toast로 출력

<img src="https://user-images.githubusercontent.com/28584258/99769148-3e22b100-2b49-11eb-9cf0-b99f6516cb00.png" alt="99639465-fb020880-2a8a-11eb-8e22-e786385bc6d6" width="300" float="left" />



**회원가입**

* 앱에서 사용할 이메일, 비밀번호, 닉네임을 설정하고 등록

<img src="https://user-images.githubusercontent.com/28584258/99769153-3f53de00-2b49-11eb-98a6-aa268f87a5e3.png" alt="99639606-28e74d00-2a8b-11eb-934e-84d4186e1361" width="300" float="left" />



**비밀번호 찾기**

* 등록된 이메일로 비밀번호를 재설정 할 수 있는 메일을 보내 새로운 비밀번호 등록
  * 등록된 이메일인 경우: 재설정 메일 전송
  * 등록되지 않은 이메일인 경우: "이메일을 확인해주세요"를 Toast로 출력

<img src="https://user-images.githubusercontent.com/28584258/99769263-66121480-2b49-11eb-98fc-ef95ab3e67c3.png" alt="99639682-40bed100-2a8b-11eb-86e6-969fa896e470" width="300" float="left" />





***



## 3. Home  screen

**텍스트 슬롯 머신**

* 앱, 웹, 알고리즘, 전공 4개의 단어 중 한 개가 랜덤으로 선택되는 슬롯 머신
  * FrameLayout과 TextView의 animate() 사용



**프로그래밍 언어 랭킹 슬라이더**

* 많이 사용되는 1~10위까지의 프로그래밍 언어와 정보를 보여주는 슬라이더
  * ViewPager, setCurrentItem, handler를 사용하여 슬라이더 제작
  * 프로그래밍 언어 순위 정보 크롤링 (참조: https://www.tiobe.com/tiobe-index/)

<img src="https://user-images.githubusercontent.com/28584258/99650504-d90f8280-2a98-11eb-8f3d-d6087e3e2828.gif" alt="2020-11-19 18-42-55 (1)" width="300" float="left" />



**내 정보**

* 사용자의 닉네임, 이메일, 내가 쓴 글, 내가 찜한 글, 내가 좋아한 글 확인 및 회원탈퇴
  * 내가 쓴 글: 사용자가 작성한 글 목록 확인
  * 내가 찜한 글: 사용자가 찜한 글 목록 확인
  * 내가 좋아한 글: 사용자가 좋아한 글 목록 확인
  * 회원탈퇴: 사용자의 이메일, 닉네임, 비밀번호 정보를 삭제

<img src="https://user-images.githubusercontent.com/28584258/99639812-706dd900-2a8b-11eb-900a-12b2addd98fc.png" alt="Screenshot_1605773688" width="300" float="left" /> <img src="https://user-images.githubusercontent.com/28584258/99639914-97c4a600-2a8b-11eb-9631-d967af503e26.png" alt="Screenshot_1605773693" width="300" float="left" /> esktop\99639975-aa3edf80-2a8b-11eb-9ef1-279888ff6222.png" alt="Screenshot_1605773701" width="300" float="left" />

<img src="https://user-images.githubusercontent.com/28584258/99769265-66aaab00-2b49-11eb-8b1d-2eaa02bca327.png" alt="99639975-aa3edf80-2a8b-11eb-9ef1-279888ff6222" width="300" float="left" />

***

## 4-0 WriteBoard screen

<img src="https://user-images.githubusercontent.com/28584275/99691084-2d802580-2acc-11eb-9668-d3971a1627a1.jpg" width="300" float="left">


## 4. WhiteBoard  screen

**사진 등록**

* 세가지의 단계를 걸쳐 게시글에 이미지 업로드

  1. 기기의 카메라, 앨범 등에 관한 권한 허가
  2. 선택된 이미지를 Firestorage에 저장 
  3. Firestorage에 저장된 이미지의 url을 Firestore에 저장

 <img src="https://user-images.githubusercontent.com/28584258/99769449-b2f5eb00-2b49-11eb-9780-d7a5c1850a3d.png" alt="99642327-baa48980-2a8e-11eb-8c26-b18e47bf47a2" width="300" float="left" />  <img src="https://user-images.githubusercontent.com/28584258/99769452-b4271800-2b49-11eb-91a7-cc5f7efd3e3b.png" alt="99642338-bd06e380-2a8e-11eb-94a6-e39c55347666" width="300" float="left" />

<img src="https://user-images.githubusercontent.com/28584258/99769454-b4bfae80-2b49-11eb-80d6-bd8ed2357698.png" alt="99642344-bed0a700-2a8e-11eb-8e17-a8057f429bd9" width="300" float="left" />

**태그**

* 각각의 게시글을 분류
  * ChipGroup을 활용한  Chip을 동적으로 생성하여 기본적인 태그 기능 구현
  * (태그 위 사진은 업로드 한 이미지를 확인하기 위해 구현)

<img src="https://user-images.githubusercontent.com/28584258/99642764-31da1d80-2a8f-11eb-8daa-de81d19f468f.png" alt="Screenshot_1605773721" width="300" float="left" />



***

## 4-1 PostView Screen

<img src="https://user-images.githubusercontent.com/28584275/99692339-8ef4c400-2acd-11eb-84b7-b10151dfad22.jpg" width="300" float="left"> <img src="https://user-images.githubusercontent.com/28584275/99692543-c3688000-2acd-11eb-9292-b03d42ff22f3.jpg"  width="300" float="left"> <img src="https://user-images.githubusercontent.com/28584275/99692772-01fe3a80-2ace-11eb-9646-0565fe04caa4.jpg"  width="300" float="left">


## 5. Board screen

**저장된 게시글 정보 불러오기**

* Firestore에 저장되어 있는 데이터를 가져옴



***



## 6. PostView screen

**좋아요, 찜**

* ImageButton을 사용하여 좋아요, 찜 버튼 생성
  * 버튼 클릭 시 Firestore에 해당 사용자의 uid를 추가, 색 변경
  * 한번 더 클릭 시 해당 사용자의 uid 삭제, 원색으로 변경

![Screenshot_1605773737](https://user-images.githubusercontent.com/28584258/99640655-8af48200-2a8c-11eb-8764-8807b03d4cff.png)
![Screenshot_1605773734](https://user-images.githubusercontent.com/28584258/99640661-8cbe4580-2a8c-11eb-8d76-7654b631d276.png)



**이미지 보기**

* Firestore에 저장되어 있는 Image 출력

  * Glider library를 사용해 image url을 ImageView의 src로 설정
  * 저장된 이미지의 개수에 맞게 ImageView 동적 생성

  <img src="https://user-images.githubusercontent.com/28584258/99640334-16214800-2a8c-11eb-97c3-6529df90418d.png" alt="Screenshot_1605773743" width="300" float="left" />



***

## 7. Run Screen
## Run Screen

<div style="text-align: center"><table><tr>
  <td style="text-align: center">
    <img src="https://user-images.githubusercontent.com/28584213/106650821-b4767180-65d6-11eb-9f23-8065701de29b.png" width="300"/>
</td>
  <td style="text-align: center">
    <img src="https://user-images.githubusercontent.com/28584213/106650964-dcfe6b80-65d6-11eb-985d-6ca55c0d974d.png" width="300"/>
</td>
</tr></table></div>
<div style="text-align: center"><table><tr>
  <td style="text-align: center">
<img src="https://user-images.githubusercontent.com/28584213/106651094-061efc00-65d7-11eb-8170-6b8efdd3ae26.png" width="300"/>
</td>
<td style="text-align: center">
<img src="https://user-images.githubusercontent.com/28584213/106651065-fdc6c100-65d6-11eb-8b42-d6817c301e72.png" width="300"/>
</td></tr></table></div>



## 8. 기타

**코루틴을 사용하여 동기, 비동기 처리**




