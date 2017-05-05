' ------------------------------------------------------------
' ↓↓↓ 環境によって変更する必要のある数値 (デフォルト値: 20)
sleepTime = 20

' ↓↓↓ 以降は変更する必要なし ↓↓↓
' ------------------------------------------------------------

if WScript.Arguments.Count < 1 then
  WScript.echo("need arguments")
  WScript.Quit(-1)
end if

id = Wscript.Arguments(0)

listCount = id / 20
varCount = id Mod 20

' ------------------------------------------------------------
' 位置ずれの修正
' ------------------------------------------------------------

if varCount = 0 Then
  listCount = listCount - 1
  varCount = 20
end if

function selectVar()
  if WshShell.AppActivate("変数の選択") then

    ' ------------------------------------------------------------
    ' 位置の初期化
    ' ------------------------------------------------------------

    WshShell.SendKeys("{HOME}")
    WshShell.SendKeys("{TAB 2}")
    WshShell.SendKeys("{HOME}")
    WshShell.SendKeys("+{TAB 2}")

    ' ------------------------------------------------------------
    ' 指定の位置まで移動
    ' ------------------------------------------------------------

    ' 左側のリストの移動
    For counter = 1 To listCount
      WshShell.SendKeys("{DOWN}")
      WScript.Sleep(sleepTime)
    Next

    WshShell.SendKeys("{TAB 2}")

    ' 右側のリストの移動
    For counter = 1 To varCount - 1
      WshShell.SendKeys("{DOWN}")
      WScript.Sleep(sleepTime)
    Next

    WshShell.SendKeys("{Enter}")

  else
    msgBox("画面遷移に失敗しました")
  end if
end function

function controlVar()
  WshShell.SendKeys("{Escape}")
  WScript.Sleep(300)
  WshShell.SendKeys(" ")
  WScript.Sleep(300)
  WshShell.SendKeys("{Tab}")
  WScript.Sleep(300)
  WshShell.SendKeys("{Enter}")
  WScript.Sleep(300)
  selectVar()
end function

' ------------------------------------------------------------
' 選択箇所の初期化
' ------------------------------------------------------------

Set WshShell=Wscript.CreateObject("Wscript.Shell")

if WshShell.AppActivate("変数の選択") then
  WshShell.SendKeys("{Escape}")
  WScript.Sleep(300)

  if WshShell.AppActivate("変数の操作") then
    WshShell.SendKeys("{Enter}")
    WScript.Sleep(500)
    selectVar()
  else
    msgBox("画面遷移に失敗しました")
  end if
else
  if WshShell.AppActivate("変数の操作") then
    controlVar()
  else
    msgBox("変数の操作画面を開いてください。")
  end if
end if

