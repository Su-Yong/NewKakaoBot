# API
카카오봇에서 지원하는 API 리스트 입니다.

# Method
지원하는 메소드 리스트 입니다.
(Bot.initData, Bot.removeData, Bot.getDataList, Bot.getContext, Util.log)
## Bot
<pre>
Bot.send(room, message); 메세지를 특정 방으로 보냅니다.
Bot.saveData(key, value); 특정값을 key를 이용하여 저장합니다.
Bot.readData(key); key를 이용해 저장한 값을 불러올 수 있습니다.
Bot.initData(key, value); key가 없으면 value로 저장하되, key가 있으면 저장하지 않습니다.
Bot.removeData(key); 데이터를 삭제합니다.
Bot.getDataList(); 모든 데이터를 json형식으로 반환합니다.
Bot.getContext(); 어플리케이션의 context를 반환합니다.
</pre>

## Util
<pre>
Util.delay(function(isSuccess), ms) 특정 밀리초 만큼 쉬었다가 소스를 실행합니다. (funciton의 파라미터는 성공여부)
Util.parseToHtml(url, option, function(data, error)) 웹사이트의 특정 DOM부분을 파싱하여 HTML로 가져옵니다. (funciton의 파라미터는 데이터 값과 오류내용)
Util.parseToText(url, option, function(data, error)) 웹사이트의 특정 DOM부분을 파싱하여 텍스트로 가져옵니다. (funciton의 파라미터는 데이터 값과 오류내용)
Util.log(title, message); 카카오봇의 로그창에 로그를 작성합니다.
</pre>

# Hooks
<pre>
funciton catchMessage(room, sender, message) 메세지가 왔을때 호출이 됩니다.
</pre>
