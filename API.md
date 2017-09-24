# API
카카오봇에서 지원하는 API 리스트 입니다

# Method
지원하는 메소드 리스트 입니다.

## Bot
<pre>
Bot.send(room, message); 메세지를 특정 방으로 보냅니다.
Bot.saveData(key, value); 특정값을 key를 이용하여 저장합니다.
Bot.readData(key);key를 이용해 저장한 값을 불러올 수 있습니다.
</pre>

# Hooks
<pre>
funciton catchMessage(room, sender, message) 메세지가 왔을때 호출이 됩니다.
</pre>
