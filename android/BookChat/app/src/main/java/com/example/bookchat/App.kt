package com.example.bookchat

import android.app.Application
import com.example.bookchat.api.ApiClient
import com.example.bookchat.api.ApiInterface
import com.example.bookchat.utils.NetworkManager

/*Application 클래스는
액티비티, 서비스와 같은 다른 기본 구성 요소를 포함하는 안드로이드 앱 내의 기본 클래스다.
Application 클래스 또는 Application 클래스의 모든 하위 클래스는
애플리케이션 / 패키지에 대한 프로세스가 생성될 때 클래스보다 먼저 인스턴스화된다. */

//Application을 상속받은 클래스는 1번째 액티비티보다 먼저 인스턴스화된다
//그래서 공동으로 관리해야 하는 데이터를 작성하기에 적합하다. (가장 먼저 인스턴스화 되니까)
//Menifests에 android:name=".상속받은 클래스명" 등록해줘야 사용가능함 안하면 RuntimeError 발생함
//어플리케이션 사이의 컴포넌트들이 공동으로 사용할 수 있기 때문에 공통되게 사용하는 내용을 작성해주면
//어디서든 context를 이용한 접근이 가능하다.

/*많은 앱에서 Application 클래스와 직접 작업할 필요는 없다.
그러나 사용자 지정 응용 프로그램 클래스에는 몇 가지 허용되는 용도가 있다.
- 첫 번째 액티비티를 만들기 전에 실행해야 하는 특수 작업
- 모든 구성 요소에서 공유해야 하는 전역 초기화(충돌 보고, 지속성)
- 공유 네트워크 클라이언트 객체 같은 불변 정적 데이터에 쉽게 접근할 수 있는 static 메서드*/


class App : Application() {

    // context를 가지지 않는곳에서 context를 필요로 할 때를 위해서 정의
    //모든 곳에서 접근해야하니까 companion object로 정의
    companion object{
        lateinit var instance : App
            private set //외부 수정 불가
    }

    lateinit var networkManager: NetworkManager
    lateinit var apiInterface :ApiInterface


    //액티비티 , 리시버 , 서비스가 생성되기 전에 어플리케이션이 시작 중일 때 실행됨
    override fun onCreate() {
        super.onCreate()
        instance = this
        inject()
    }

    private fun inject() {
        networkManager = NetworkManager()
        apiInterface = ApiClient.getApiClient().create(ApiInterface::class.java)

    }
}