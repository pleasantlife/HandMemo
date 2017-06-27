# 손글씨 메모 (HandMemo)

## 앱의 주요 기능 (Key feature of HandMemo application)

 - 사용자가 화면을 터치하면, 그에 따른 궤적을 선으로 나타냄.
   (App recognize user's input on screen and make a line.)
 - 선마다 여러가지 색상과 굵기를 새롭게 부여할 수 있음.
   (Give different color and stroke every single line.)
 - 새로운 선을 그릴 때 이전의 선은 사라지지 않음.
   (When draw a new line, don't disappear other lines.)
 - 화면캡처 기능을 통해 메모를 (갤러리에) 저장할 수 있음.
   (Can save memos using screen-capture feature.)

## 앱 제작을 위해 알아야할 안드로이드의 구현 기능

 1. Thread : 새로운 선을 그릴 떄 이전의 선이 사라지지 않기 위해 병렬처리.
 2. Draw : 선이나 도형등을 그릴 수 있는 안드로이드의 기본 기능.
