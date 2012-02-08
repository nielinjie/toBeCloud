/* Author:

*/

$(function(){
	$('#peer-refresh-button').click(function() {
		peer_refresh();
		return false;
	})
	
	$('#history-refresh-button').click(function() {
		history_refresh();
		return false;
	})
});

function peer_refresh() {
	$.getJSON('/ui/peers', function(peers) {
		$('#peer-list').empty()
		$.each(peers, function(index, peer) {
			peer_display(peer).appendTo('#peer-list')
		})
	});
}
function peer_display(peer) {
	var a = $("<a href='#'></a>").text(peer.ip +":"+peer.port).click(function() {
		diff_refresh(peer);
		return false
	})
	return $('<li></li>').append(a);
}
function diff_refresh(peer) {
	$.getJSON('/ui/diff',{"peer":peer.ip+":"+peer.port},function(diffs){
		$('#diff-list').empty()
		$.each(diffs.diff, function(index, diff){
			diff_item(diff,peer).appendTo('#diff-list')
		})
	})
	
}
function diff_item(diff,peer) {
	return $('<li></li>').append(
		$('<a href="#"></a>').text(diff.source.relativePath).click(
			function(){
				download(diff,peer)
				return false
			}
		)
	)
}
function download(diff,peer) {
	$.get('/ui/download',{"peer":peer.ip+":"+peer.port,
		"source.mountName":diff.source.mountName,
		"source.relativePath":diff.source.relativePath,
		"dist.mountName":diff.dist.mountName,
		"dist.file":diff.dist.file
		})
}

function history_refresh() {
	$.getJSON('/ui/history',function(history){
		$('#history-list').empty()
		$.each(history, function(index, history){
			$('<li></li>').text(history).appendTo('#history-list')
		})
	})
	
}