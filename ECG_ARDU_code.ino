#include <SoftwareSerial.h>

// Bluetooth 모듈 핀 설정
int Rx = 7;
int Tx = 8;
SoftwareSerial HM10 (Tx, Rx);

// ECG 센서 핀 설정
int BEAT = 2;
int ECG = A5;

int i = 0;

// 시간
unsigned long past = 0; // 과거 시간 저장 변수
unsigned long adjust = 0; // start를 입력 받은 경우 현재 시간을 보정함

// 앱으로부터 수신 받으면 측정 수행
bool flag = false;

void setup() {
  Serial.begin(9600);
  HM10.begin(9600);
}

void loop() {
  unsigned long now = millis(); // 현재 시간을 저장

  int data_ECG = analogRead(ECG);
  int data_BEAT = digitalRead(BEAT);

  if(HM10.available()){
    // 어플에서 시작버튼을 누르면 모든 동작을 시작함
    if (HM10.read() == '1') {
      flag = true;
      adjust = now;
    }
    else flag = false;
  }

  if(flag){
    // 값 모니터링용
    if (0) {
      Serial.print(300);
      Serial.print(",");
      Serial.print(data_BEAT);
      Serial.print(",");
      Serial.println(data_ECG);
    }  
    
    // 심박이 측정되면
    if (data_BEAT) {
      int diff = now - past; // 현재 심장이 뛴 시각과 과거 심박이 뛴 시간을 뺀 값을 저장함
      past = now; // 마지막으로 심장이 뛴 시각을 현재 시각으로 초기화

      // 노이즈가 걸리면 무시함
      if (0 <= diff  && diff < 500) {
        // 안정되지 않았다는 뜻으로 0.05초 후에 재시도
        delay(50);
        past = now;
      }
      // 그 외 정상적인 심박수
      else {
        float bpm = 60000.0 / diff; // R-R Interval을 BPM으로 변환 (float)

        // 정상 심박이면 값 출력, 아니면 무시
        // 최저 심박 (49)는 논문에서 발췌한 운동선수의 휴식간 심박수
        if (49 < (int)bpm){
          Serial.println(diff);
          //Serial.println(bpm);

          // 데이터 전송
          HM10.print("T_");
          HM10.print(now-adjust);
          HM10.print("/D-");
          HM10.print(diff);
        }
      }
    }
  }
} 