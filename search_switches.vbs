' ------------------------------------------------------------
' ������ ���ɂ���ĕύX����K�v�̂��鐔�l (�f�t�H���g�l: 20)
sleepTime = 20

' ������ �ȍ~�͕ύX����K�v�Ȃ� ������
' ------------------------------------------------------------

if WScript.Arguments.Count < 1 then
  WScript.echo("need arguments")
  WScript.Quit(-1)
end if

id = Wscript.Arguments(0)

listCount = id / 20
varCount = id Mod 20

' ------------------------------------------------------------
' �ʒu����̏C��
' ------------------------------------------------------------

if varCount = 0 Then
  listCount = listCount - 1
  varCount = 20
end if

function selectVar()
  if WshShell.AppActivate("�X�C�b�`�̑I��") then

    ' ------------------------------------------------------------
    ' �ʒu�̏�����
    ' ------------------------------------------------------------

    WshShell.SendKeys("{HOME}")
    WshShell.SendKeys("{TAB 2}")
    WshShell.SendKeys("{HOME}")
    WshShell.SendKeys("+{TAB 2}")

    ' ------------------------------------------------------------
    ' �w��̈ʒu�܂ňړ�
    ' ------------------------------------------------------------

    ' �����̃��X�g�̈ړ�
    For counter = 1 To listCount
      WshShell.SendKeys("{DOWN}")
      WScript.Sleep(sleepTime)
    Next

    WshShell.SendKeys("{TAB 2}")

    ' �E���̃��X�g�̈ړ�
    For counter = 1 To varCount - 1
      WshShell.SendKeys("{DOWN}")
      WScript.Sleep(sleepTime)
    Next

    WshShell.SendKeys("{Enter}")

  else
    msgBox("��ʑJ�ڂɎ��s���܂���")
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
' �I���ӏ��̏�����
' ------------------------------------------------------------

Set WshShell=Wscript.CreateObject("Wscript.Shell")

if WshShell.AppActivate("�X�C�b�`�̑I��") then
  WshShell.SendKeys("{Escape}")
  WScript.Sleep(300)

  if WshShell.AppActivate("�X�C�b�`�̑���") then
    WshShell.SendKeys("{Enter}")
    WScript.Sleep(500)
    selectVar()
  else
    msgBox("��ʑJ�ڂɎ��s���܂���")
  end if
else
  if WshShell.AppActivate("�X�C�b�`�̑���") then
    controlVar()
  else
    msgBox("�X�C�b�`�̑����ʂ��J���Ă��������B")
  end if
end if

