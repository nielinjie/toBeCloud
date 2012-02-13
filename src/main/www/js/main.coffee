$ ->
  $('#peer-refresh-button').click -> 
    peer_refresh()
    return false
  $('#history-refresh-button').click ->
    history_refresh()
    return false
    
peer_refresh = ->
  $.getJSON '/ui/peers', (peers)->
    $('#peer-list').empty()
    peer_display(peer).appendTo('#peer-list') for peer in peers
    
peer_display = (peer) ->
  doto $('<li></li>').text(peer_string(peer)), (li)->
    diff_refresh(peer,li)
  
diff_refresh = (peer,peerLi) ->
  $.getJSON '/ui/diff', "peer":peer_string(peer), (diffs) -> 
      ul= $('<ul></ul>')
      diff_item(diff,peer).appendTo(ul) for diff in diffs.diff
      ul.appendTo(peerLi)
      
diff_item = (diff,peer) ->
  $('<li></li>').append(
    $('<a href="#"></a>').text(diff.source.relativePath).click ->
      download(diff,peer)
      return false
  )

download = (diff,peer) ->
  $.get('/ui/download', 
    "peer":peer_string(peer),
    "source.mountName":diff.source.mountName,
    "source.relativePath":diff.source.relativePath,
    "dist.mountName":diff.dist.mountName,
    "dist.file":diff.dist.file
    )

history_refresh = ->
  $.getJSON '/ui/history', (history)->
    $('#history-list').empty()
    $('<li></li>').text(his).appendTo('#history-list') for his in history
        
  
peer_string = (peer) ->
  "#{peer.ip}:#{peer.port}"
        
doto = (obj, fn) -> 
  fn(obj)
  obj
