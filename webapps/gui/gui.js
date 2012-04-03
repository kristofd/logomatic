$(document).ready(function() {

	setupForms();

	var ticks = {
		month: [1, "month"],
		week: [7, "day"],
		day: [1, "day"],
		hour: [1, "hour"],
		minute: [1, "minute"],
		minute: [1, "second"],
	}
	
	var widths = {
		month: 1000 * 60 * 60 * 24 * 15,
		week: 1000 * 60 * 60 * 24 * 3,
		day: 1000 * 60 * 60 * 20,
		hour: 1000 * 60 * 50,
		minute: 1000 * 50,
		second: 800
	}

	var FACET_LABELS = 0;
	var FACET_VALUES = 1;
	
	var requestHandler = "/solr/select/?";
	var initialParams = "rows=0&indent=on&wt=json&facet=true&facet.sort=false&facet.method=enum&f.date.timestamp.mincount=0&facet.limit=-1&facet.date=timestamp&f.filetype.facet.mincount=50&facet.field=client&f.client.facet.limit=50&f.client.facet.sort=true&facet.field=url&f.url.facet.limit=50&f.url.facet.sort=true";

	var query = "";
	var params = "";

	var maxValue = 0;

	setupParams();
	sendQuery(requestHandler+initialParams+query+params);


	$('body').keypress(function(event) {
		if (event.keyCode == '13') {
			$("#form").submit();
		}
	});

	$("#form").submit(function() {
		setupParams();
		sendQuery(requestHandler+initialParams+query+params);
		
		$("#filtervalues").html("");
		return false;
	});

	function setupParams() {
		query = "&q=timestamp:[" + $("#from").val() + " TO " + $("#to").val() + "]";
		params = "&facet.date.start=" + $("#from").val() + "&facet.date.end=" + $("#to").val();
		params += "&facet.date.gap=" + $("#resolution").val() + "&facet.field=" + $("#groupfield").val();

		var filter = $("#filterfield").val();
		if (filter) {
			params += "&facet.field={!ex=filter}" + filter;

			var boxes = $("#filtervalues :checked");
			if (boxes.length > 0) {
				params += "&fq={!tag=filter}";
				for (var i=0; i<boxes.length; i++) {
					if (i>0) params += " ";
					params += filter + ":" + boxes[i].value;
				}
			}
		}
		
		params += "&filterfield=" + filter + "&groupfield=" + $("#groupfield").val();

	}

	function setupForms() {
		var from = $('#from');
		from.datetimepicker({
			dateFormat: 'yy-mm-dd',
			separator: 'T',
			firstDay: 1,
			showSecond: true,
			timeFormat: 'hh:mm:ssZ'
		});
		from.datetimepicker('setDate', (new Date(2011, 7, 1, 0, 0)));

		var to = $('#to');
		to.datetimepicker({
			dateFormat: 'yy-mm-dd',
			separator: 'T',
			firstDay: 1,
			showSecond: true,
			timeFormat: 'hh:mm:ssZ'
		});
		to.datetimepicker('setDate', (new Date(2011, 8, 1, 0, 0)));
		
		$("#groupfield").val("responsecode");
	}

	function sendQuery(q) {
		$("#debug").html(q);

		$.getJSON(q, function(data) {
			var timeSeries = createDateArray(data.facet_counts.facet_dates.timestamp);
			plotGraph(timeSeries);
			
			var facetField = data.responseHeader.params['groupfield'];
			var facetValues = data.facet_counts.facet_fields;
			var currentFacets = facetValues[facetField];
			plotSecondaryGraph(createArray(currentFacets, FACET_VALUES), createArray(currentFacets, FACET_LABELS));

			var filterField = data.responseHeader.params['filterfield'];
			if (filterField && filterField.length > 0) {
				$("#filtervalues").html(createFacet(filterField, facetValues[filterField]));
			}

			var filterString = data.responseHeader.params['fq'];
			if (filterString) {
				var filterValues = parseFilterValues(filterField, filterString);
				for (var v in filterValues) {
					$("input[value=" + filterValues[v] + "]").attr("checked", true);		
				}
			}

			var ipList = data.facet_counts.facet_fields.client;
			if (ipList) {
				$("#iplist").html(createList(ipList));
			}

			var urlList = data.facet_counts.facet_fields.url;
			if (urlList) {
				$("#urllist").html(createList(urlList));
			}

			var units = calculateUnits($("#from").val(), $("#to").val(), $("#resolution :selected").text());
			var average = Math.floor(data.response.numFound / units);

			$("#total").html(data.response.numFound);
			$("#maxvalue").html(maxValue + " reqs/" + $("#resolution :selected").text());
			$("#avgvalue").html(average + " reqs/" + $("#resolution :selected").text());
			$("#responsetime").html(data.responseHeader.QTime + " ms");
		});
	}
	
	function calculateUnits(from, to, resolution) {
		var fromDate = new Date(from);
		var toDate = new Date(to);

		if (resolution === 'month') {
			return toDate.getMonth() - fromDate.getMonth();
		}

		if (resolution === 'week') {
			return toDate.getWeekOfYear() - fromDate.getWeekOfYear();
		}

		var timePeriod = new TimePeriod(fromDate, toDate);

		var value = timePeriod.days;
		
		if (resolution === 'day') {
			return value;
		}

		value = value * 24 + timePeriod.hours;
		if (resolution === 'hour') {
			return value;
		}

		value = value * 60 + timePeriod.minutes;
		if (resolution === 'minute') {
			return value;
		}

		value = value * 60 + timePeriod.seconds;
		if (resolution === 'second') {
			return value;
		}
	
		return 1;
	}

	function plotGraph(data) {
		var points = {
			color: "#000000",
			data: data,
			bars: { 
				show: true,
				lineWidth: 0,
				barWidth: widths[$("#resolution :selected").text()],
				align: "center"
			}
		};
	
		var options = {
			xaxis: { 
				mode: "time",
				minTickSize: ticks[$("#resolution :selected").text()] 
			}
		};

		$.plot($("#graph"), [points], options);	
	}	

	function plotSecondaryGraph(data, ticks) {
		var points = {
			color: "#000000",
			data: data,
			bars: { 
				show: true,
				lineWidth: 0,
				align: "center"
			}
		};
	
		var options = {
			xaxis: { 
				ticks: ticks
			}
		};
	
		$.plot($("#secondarygraph"), [points], options);	
	}	
	
	function parseFilterValues(filterName, filterString) {
		var array = [];
		filterString = filterString.substring(filterString.indexOf("}") + 1);
		filterString.split(" ").map(function(v) {
			array.push(v.replace(filterName + ":", ""));
		});
		return array;
	}
	
	function createArray(valueList, step) {
		var array = [];
		for (var i=0; i<valueList.length; i+=2) {
			array.push([i/2, valueList[i + step]]);
		}	
		return array;	
	}

	function createFacet(facetName, valueList) {
		var html="<legend>" + facetName + "</legend><ul>";
		for (var i=0; i<valueList.length; i+=2) {
			html += "<li><input type=\"checkbox\" name=\"" + facetName + "\" value=\"" + valueList[i] + "\" id=\"" + valueList[i] + "\">";
			html += "<label for=\"" + valueList[i] + "\">" + valueList[i] + " (" + valueList[i+1] + ")</label></li>";
		}	
		return html + "</ul>";
	}

	function createList(valueList) {
		var html="<ul>";
		for (var i=0; i<valueList.length; i+=2) {
			html += "<li>" + valueList[i] + " (" + valueList[i+1] + ")<li/>";
		}	
		return html + "</ul>";
	}

	function createDateArray(valueList) {
		var array = [];
		maxValue = 0;
		for (var v in valueList) {
			if (v[v.length-1] === "Z") {
				if (valueList[v] > maxValue) maxValue = valueList[v];
				var d = Date.parseExact(v, "yyyy-MM-ddTHH:mm:ssZ");
				d.addHours(2);
				array.push([d.getTime(), valueList[v]]);
			}
		}
		return array;
	}
});

// refactoring
// increase resolution when time interval is too small
// date pickers
// pick date interval (start + X days/months/weeks/hours/minutes)
// use stats (hits, bytes downloaded)
// geoip

// list logline values (urls, f.ex) for selected data set